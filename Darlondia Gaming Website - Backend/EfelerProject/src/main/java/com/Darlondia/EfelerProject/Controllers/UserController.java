package com.Darlondia.EfelerProject.Controllers;

import com.Darlondia.EfelerProject.Models.Game;
import com.Darlondia.EfelerProject.Models.GameList;
import com.Darlondia.EfelerProject.Models.Review;
import com.Darlondia.EfelerProject.Models.User;
import com.Darlondia.EfelerProject.Services.GameService;
import com.Darlondia.EfelerProject.Services.ReviewService;
import com.Darlondia.EfelerProject.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {

        // Check for validation errors in the request body
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        // Check if the username is available
        if (!userService.isUsernameAvailable(user.getUsername())) {
            return new ResponseEntity<>("Username already taken", HttpStatus.CONFLICT);
        }

        // Check if the email is valid and available
        if (!userService.isEmailValid(user.getEmail())) {
            return new ResponseEntity<>("Invalid email address", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isEmailAvailable(user.getEmail())) {
            return new ResponseEntity<>("Email address already in use", HttpStatus.CONFLICT);
        }

        // Check if the password meets the regex format and matches the confirmation
        if (!userService.isPasswordValid(user.getPasswordConfirmation())) {
            return new ResponseEntity<>("Invalid password format", HttpStatus.BAD_REQUEST);
        }
        if (!user.getPasswordConfirmation().equals(user.getPassword())) {
            return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
        }

        // Set the terms and conditions as accepted
        user.setTermsAccepted(true);

        // Create the user using the UserService
        userService.createUser(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(HttpServletRequest request, @Valid @RequestBody User user, BindingResult result) {
        // Check for validation errors in the request body
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        // Check if the provided credentials are correct
        if (!userService.checkCredentials(user.getUsername(), user.getPassword())) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        // Get the authenticated user object from the database
        User authenticatedUser = userService.findByUsername(user.getUsername());

        // Create a new session and set the user's authentication credentials
        HttpSession session = request.getSession();
        session.setAttribute("authenticatedUser", authenticatedUser);

        // Remove the password from the user object before sending it to the client
        authenticatedUser.setPassword(null);

        return new ResponseEntity<>(authenticatedUser, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("authenticatedUser") != null) {
            session.invalidate();
            return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/follow/{userIdToFollow}")
    public ResponseEntity<?> followUser(HttpServletRequest request, @PathVariable String userIdToFollow) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        loggedInUser = userService.findById(loggedInUser.getId()); // Fetch the most recent loggedInUser data
        User userToFollow = userService.findById(userIdToFollow);

        if (userToFollow == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (loggedInUser.getFollowing().contains(userToFollow.getId())) {
            return new ResponseEntity<>("Already following the user", HttpStatus.BAD_REQUEST);
        }

        userService.followUser(loggedInUser, userToFollow);
        return new ResponseEntity<>("User followed successfully", HttpStatus.OK);
    }

    @PostMapping("/unfollow/{userIdToUnfollow}")
    public ResponseEntity<?> unfollowUser(HttpServletRequest request, @PathVariable String userIdToUnfollow) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        loggedInUser = userService.findById(loggedInUser.getId()); // Fetch the most recent loggedInUser data
        User userToUnfollow = userService.findById(userIdToUnfollow);

        if (userToUnfollow == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (!loggedInUser.getFollowing().contains(userToUnfollow.getId())) {
            return new ResponseEntity<>("Not following the user", HttpStatus.BAD_REQUEST);
        }

        userService.unfollowUser(loggedInUser, userToUnfollow);
        return new ResponseEntity<>("User unfollowed successfully", HttpStatus.OK);
    }

    @PostMapping("/changeUsername")
    public ResponseEntity<?> changeUsername(HttpServletRequest request, @RequestBody Map<String, String> newUsernameMap) {
        String newUsername = newUsernameMap.get("newUsername");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User currentUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = currentUser.getUsername();
        if (userService.changeUsername(currentUsername, newUsername)) {
            currentUser.setUsername(newUsername);
            session.setAttribute("authenticatedUser", currentUser);
            return new ResponseEntity<>("Username changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to change username", HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/changeEmail")
    public ResponseEntity<?> changeEmail(HttpServletRequest request, @RequestBody Map<String, String> newEmailMap) {
        String newEmail = newEmailMap.get("newEmail");
        if (!userService.isEmailValid(newEmail)) {
            return new ResponseEntity<>("Invalid email address", HttpStatus.BAD_REQUEST);
        }
        if (!userService.isEmailAvailable(newEmail)) {
            return new ResponseEntity<>("Email address already in use", HttpStatus.CONFLICT);
        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User currentUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = currentUser.getUsername();
        if (userService.changeEmail(currentUsername, newEmail)) {
            return new ResponseEntity<>("Email changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to change email", HttpStatus.BAD_REQUEST);
        }
    }

    // Line 208 needs to be updated -Ugur
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestBody Map<String, String> newPasswords) {
        String oldPassword = newPasswords.get("oldPassword");
        String newPassword = newPasswords.get("newPassword");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }
        User currentUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = currentUser.getUsername();
        if (userService.changePassword(currentUsername, oldPassword, newPassword)) {
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unable to change password", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        User user = userService.findByUsername(loggedInUser.getUsername());
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("username", user.getUsername());
        profileData.put("accountCreationDate", user.getAccountCreationDate());
        profileData.put("bio", user.getBio());
        profileData.put("profilePicture", user.getProfilePicture());

        return new ResponseEntity<>(profileData, HttpStatus.OK);
    }
    @PostMapping("/updateBio")
    public ResponseEntity<?> updateBio(HttpServletRequest request, @RequestBody Map<String, String> bioData) {
        String newBio = bioData.get("newBio");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = loggedInUser.getUsername();
        userService.updateBio(currentUsername, newBio);
        return new ResponseEntity<>("Bio updated successfully", HttpStatus.OK);
    }
    @PostMapping("/uploadProfilePicture")
    public ResponseEntity<?> uploadProfilePicture(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user logged in");
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = loggedInUser.getUsername();

        try {
            userService.uploadProfilePicture(currentUsername, file);
            return ResponseEntity.status(HttpStatus.OK).body("Profile picture uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading profile picture: " + e.getMessage());
        }
    }
    @GetMapping("/downloadProfilePicture/{username}")
    public ResponseEntity<Resource> downloadProfilePicture(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            String fileName = user.getProfilePicture();
            Resource resource = userService.downloadProfilePicture(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/jpeg"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception ex) {
            // Log the exception and return a meaningful error message
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Error: " + ex.getMessage()).getBytes()));
        }
    }

    @PostMapping("/gameList/create")
    public ResponseEntity<?> createGameList(HttpServletRequest request, @RequestParam String name) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = loggedInUser.getUsername();

        GameList createdGameList = userService.createGameList(currentUsername, name);

        if (createdGameList == null) {
            return new ResponseEntity<>("Error creating the game list", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(createdGameList, HttpStatus.CREATED);
    }
    @DeleteMapping("/gameList/{gameListId}")
    public ResponseEntity<?> deleteGameList(HttpServletRequest request, @PathVariable String gameListId) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = loggedInUser.getUsername();

        userService.deleteGameList(currentUsername, gameListId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/gameList/update/{gameListId}")
    public ResponseEntity<?> updateGameList(HttpServletRequest request, @PathVariable String gameListId, @RequestParam String newName) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUsername = loggedInUser.getUsername();

        GameList updatedGameList = userService.updateGameList(currentUsername, gameListId, newName);

        if (updatedGameList == null) {
            return new ResponseEntity<>("Game list not found or not owned by the user", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedGameList, HttpStatus.OK);
    }
    @PostMapping("/gameList/{gameListId}/addGame/{gameId}")
    public ResponseEntity<?> addGameToList(HttpServletRequest request, @PathVariable String gameListId, @PathVariable String gameId) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUserId = loggedInUser.getId();

        boolean gameAdded = userService.addGameToList(currentUserId, gameListId, gameId);

        if (!gameAdded) {
            return new ResponseEntity<>("Game is already in the list or error adding the game to the list", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Game added successfully", HttpStatus.OK);
    }

    @DeleteMapping("/gameList/{gameListId}/deleteGame/{gameId}")
    public ResponseEntity<?> deleteGameFromList(HttpServletRequest request, @PathVariable String gameListId, @PathVariable String gameId) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUser") == null) {
            return new ResponseEntity<>("No user logged in", HttpStatus.UNAUTHORIZED);
        }

        User loggedInUser = (User) session.getAttribute("authenticatedUser");
        String currentUserId = loggedInUser.getId();

        boolean gameDeleted = userService.deleteGameFromList(currentUserId, gameListId, gameId);

        if (!gameDeleted) {
            return new ResponseEntity<>("Error deleting the game from the list", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Game deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/userProfile/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        // Find the user by the username
        User user = userService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Fetch the user's game lists
        List<GameList> gameLists = userService.findGameListsByUser(user);

        // Fetch the user's reviews
        List<Review> reviews = reviewService.findReviewsByUser(user);

        // Prepare the response data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", user.getUsername());
        responseData.put("bio", user.getBio());
        responseData.put("profilePicture", user.getProfilePicture());
        responseData.put("accountCreationDate", user.getAccountCreationDate());
        responseData.put("gameLists", gameLists);

        List<Map<String, Object>> reviewDataList = new ArrayList<>();
        for (Review review : reviews) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("reviewId", review.getId());
            reviewData.put("content", review.getContent());
            reviewData.put("rating", review.getRating());
            reviewData.put("createdAt", review.getCreatedAt());

            // Get the game name from the review
            Game game = gameService.getGameById(review.getGameId()).orElse(null);
            if (game != null) {
                String gameName = game.getName();
                reviewData.put("gameName", gameName);
            }


            reviewDataList.add(reviewData);
        }
        responseData.put("reviews", reviewDataList);
        responseData.put("id", user.getId());

        // Get the names of the followers
        List<String> followerNames = new ArrayList<>();
        for (String followerId : user.getFollowers()) {
            User follower = userService.findById(followerId);
            if (follower != null) {
                followerNames.add(follower.getUsername());
            }
        }
        responseData.put("followers", followerNames);

        // Get the names of the following
        List<String> followingNames = new ArrayList<>();
        for (String followingId : user.getFollowing()) {
            User followedUser = userService.findById(followingId);
            if (followedUser != null) {
                followingNames.add(followedUser.getUsername());
            }
        }
        responseData.put("following", followingNames);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/usernames")
    public ResponseEntity<List<String>> getAllUsernames() {
        List<String> usernames = userService.getAllUsernames();
        return new ResponseEntity<>(usernames, HttpStatus.OK);
    }
    @GetMapping("/userProfile/{username}/gameLists")
    public ResponseEntity<?> getUserGameLists(@PathVariable String username) {
        // Find the user by the username
        User user = userService.findByUsername(username);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        // Fetch the user's game lists
        List<GameList> gameLists = userService.findGameListsByUser(user);

        // Return the game lists
        return new ResponseEntity<>(gameLists, HttpStatus.OK);
    }
    @GetMapping("/users/lists")
    public ResponseEntity<List<GameList>> getAllUsersLists() {
        List<GameList> lists = userService.getAllUsersLists();
        return new ResponseEntity<>(lists, HttpStatus.OK);
    }
    @GetMapping("/lists/{id}")
    public ResponseEntity<?> getListDetails(@PathVariable String id) {
        Map<String, Object> gameList = userService.getGameListById(id);

        if (gameList == null || gameList.isEmpty()) {
            return new ResponseEntity<>("Game list not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(gameList, HttpStatus.OK);
    }

}