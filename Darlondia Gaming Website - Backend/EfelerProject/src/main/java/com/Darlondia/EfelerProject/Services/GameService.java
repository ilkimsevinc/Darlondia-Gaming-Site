package com.Darlondia.EfelerProject.Services;
// Add import
import com.Darlondia.EfelerProject.Models.Review;
import com.Darlondia.EfelerProject.Services.ReviewService;
import com.Darlondia.EfelerProject.Models.Game;
import com.Darlondia.EfelerProject.Repositories.GameRepository;
import edu.stanford.nlp.simple.Sentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ReviewService reviewService;
    public static final String FILE_STORAGE_LOCATION = "src/main/resources/Images";

    @Autowired
    public GameService(GameRepository gameRepository, ReviewService reviewService) {
        this.gameRepository = gameRepository;
        this.reviewService = reviewService;
    }

    public Game createGame(String name, LocalDate releaseDate, String platform, String genre, String description, MultipartFile file) {
        String fileName = handleFileUpload(file);

        Game game = new Game();
        game.setName(name);
        game.setReleaseDate(releaseDate);
        game.setPlatform(platform);
        game.setGenre(genre);
        game.setDescription(description);
        game.setPictureUrl(fileName);

        return gameRepository.save(game);
    }


    private String handleFileUpload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path storagePath = Paths.get(FILE_STORAGE_LOCATION).toAbsolutePath().normalize();

        try {
            Files.createDirectories(storagePath);
            Path targetLocation = storagePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Could not save file: " + fileName, ex);
        }

        return fileName;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(String id) {
        Optional<Game> gameOptional = gameRepository.findById(id);
        gameOptional.ifPresent(game -> {
            List<Review> reviews = reviewService.getReviewsByGameId(id);
            game.setReviews(reviews);
        });
        return gameOptional;
    }


    public Optional<Game> updateGame(String id, Game updatedGame) {
        return gameRepository.findById(id)
                .map(game -> {
                    game.setName(updatedGame.getName());
                    game.setReleaseDate(updatedGame.getReleaseDate());
                    game.setPlatform(updatedGame.getPlatform());
                    game.setGenre(updatedGame.getGenre());
                    game.setPictureUrl(updatedGame.getPictureUrl());
                    game.setDescription(updatedGame.getDescription()); // Added description field update
                    return gameRepository.save(game);
                });
    }
    public Path getImageFilePath(String fileName) {
        Path storagePath = Paths.get(FILE_STORAGE_LOCATION).toAbsolutePath().normalize();
        return storagePath.resolve(fileName);
    }

    public void deleteGame(String id) {
        gameRepository.deleteById(id);
    }

    public List<Game> searchGamesByName(String name) {
        // Use Stanford NLP to tokenize and lemmatize the search query
        List<String> queryTokens = new Sentence(name).lemmas();

        // Retrieve all games from the database
        List<Game> allGames = gameRepository.findAll();

        // Filter games based on partial string matching using lemmas
        return allGames.stream()
                .filter(game -> containsPartialMatch(game.getName(), queryTokens))
                .collect(Collectors.toList());
    }

    private boolean containsPartialMatch(String gameName, List<String> queryTokens) {
        for (String token : queryTokens) {
            if (!gameName.toLowerCase().contains(token.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

}

