package com.Darlondia.EfelerProject.Controllers;

import com.Darlondia.EfelerProject.Models.Game;
import com.Darlondia.EfelerProject.Models.Review;
import com.Darlondia.EfelerProject.Models.User;
import com.Darlondia.EfelerProject.Repositories.GameRepository;
import com.Darlondia.EfelerProject.Services.GameService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.Darlondia.EfelerProject.Services.ReviewService;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;
    private final ReviewService reviewService;
    private final GameRepository gameRepository;  // Add this line

    @Autowired
    public GameController(GameService gameService, ReviewService reviewService, GameRepository gameRepository) {
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.gameRepository = gameRepository;  // Add this line
    }

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestParam("name") String name,
                                           @RequestParam("releaseDate") String releaseDateString,
                                           @RequestParam("platform") String platform,
                                           @RequestParam("genre") String genre,
                                           @RequestParam("description") String description, // CHANGED: Added description field
                                           @RequestParam("file") MultipartFile file) {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate releaseDate = LocalDate.parse(releaseDateString, dateFormatter);

        Game createdGame = gameService.createGame(name, releaseDate, platform, genre, description, file);
        return new ResponseEntity<>(createdGame, HttpStatus.CREATED);
    }

    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        Path imagePath = gameService.getImageFilePath(fileName);
        Resource imageResource;

        try {
            imageResource = new UrlResource(imagePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not get image with file name: " + fileName, e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Change this if your images are in another format like PNG, GIF, etc.
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageResource.getFilename() + "\"")
                .body(imageResource);
    }


    @GetMapping("/list")
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable String id) {
        Optional<Game> gameOptional = gameService.getGameById(id);

        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            return new ResponseEntity<>(game, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable String id, @RequestBody Game updatedGame) {
        Game game = gameService.updateGame(id, updatedGame)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable String id) {
        gameService.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{gameId}/reviews/create")
    public ResponseEntity<Review> createReview(HttpServletRequest request, @PathVariable String gameId,
                                               @RequestBody Review review) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String userId = loggedInUser.getId();
        String content = review.getContent();
        int rating = review.getRating();

        Review createdReview = reviewService.createReview(gameId, userId, content, rating);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @PutMapping("/{gameId}/reviews/update/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable String gameId, @PathVariable String reviewId,
                                          @RequestBody Review review, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUserId = loggedInUser.getId();

        Optional<Review> updatedReviewOptional = reviewService.updateReview(reviewId, currentUserId, review.getContent(), review.getRating());
        if (updatedReviewOptional.isPresent()) {
            return new ResponseEntity<>(updatedReviewOptional.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Review not found or not owned by the user", HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{gameId}/reviews/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String gameId, @PathVariable String reviewId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUserId = loggedInUser.getId();

        try {
            reviewService.deleteReview(reviewId, currentUserId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>("Review not found or not owned by the user", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Game>> searchGamesByName(@RequestParam("name") String name) {
        List<Game> searchResults = gameService.searchGamesByName(name);

        // Filter the search results to include only games whose names match the search query (case-insensitive)
        List<Game> matchedGames = searchResults.stream()
                .filter(game -> game.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        if (matchedGames.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(matchedGames, HttpStatus.OK);
    }

}
