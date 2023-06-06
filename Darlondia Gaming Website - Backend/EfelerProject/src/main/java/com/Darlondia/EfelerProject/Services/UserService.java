package com.Darlondia.EfelerProject.Services;

import com.Darlondia.EfelerProject.Models.Game;
import com.Darlondia.EfelerProject.Models.GameList;
import com.Darlondia.EfelerProject.Models.User;
import com.Darlondia.EfelerProject.Repositories.GameListRepository;
import com.Darlondia.EfelerProject.Repositories.GameRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.Darlondia.EfelerProject.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;
import java.security.SecureRandom;
import java.util.Optional;
// Add the imports here
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    public static final String FILE_STORAGE_LOCATION = "src/main/resources/Images";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // Add this field to the class
    @Autowired
    private  GameListRepository gameListRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }

    public boolean isUsernameAvailable(String username) {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        return !user.isPresent();
    }

    public boolean isEmailValid(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }

    public boolean isEmailAvailable(String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        return !user.isPresent();
    }

    public boolean isPasswordValid(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        return password.matches(regex);
    }

    public void createUser(User user) {
        user.setAccountCreationDate(LocalDateTime.now());
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRole("USER"); // Set the "USER" role for new users
        userRepository.save(user);
    }

    public boolean checkCredentials(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    // New methods for following and unfollowing users
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public void followUser(User follower, User userToFollow) {
        // Retrieve the latest version of the users from the database
        User latestFollower = userRepository.findById(follower.getId()).orElse(null);
        User latestUserToFollow = userRepository.findById(userToFollow.getId()).orElse(null);

        // Check if the users exist in the database
        if (latestFollower == null || latestUserToFollow == null) {
            throw new IllegalStateException("User not found in the database");
        }

        latestFollower.getFollowing().add(latestUserToFollow.getId());
        latestUserToFollow.getFollowers().add(latestFollower.getId());
        userRepository.save(latestFollower);
        userRepository.save(latestUserToFollow);
    }

    public void unfollowUser(User follower, User userToUnfollow) {
        // Retrieve the latest version of the users from the database
        User latestFollower = userRepository.findById(follower.getId()).orElse(null);
        User latestUserToUnfollow = userRepository.findById(userToUnfollow.getId()).orElse(null);

        // Check if the users exist in the database
        if (latestFollower == null || latestUserToUnfollow == null) {
            throw new IllegalStateException("User not found in the database");
        }

        latestFollower.getFollowing().remove(latestUserToUnfollow.getId());
        latestUserToUnfollow.getFollowers().remove(latestFollower.getId());
        userRepository.save(latestFollower);
        userRepository.save(latestUserToUnfollow);
    }


    public boolean isEmailExists(String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));
        return user.isPresent();
    }

    public String generateResetCode() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public void saveResetCode(String email, String resetCode) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setResetCode(resetCode);
            user.setResetCodeExpiration(LocalDateTime.now().plusHours(1));
            userRepository.save(user);
        }
    }

    public boolean isResetCodeValid(String resetCode) {
        User user = userRepository.findByResetCode(resetCode);
        if (user == null) {
            return false;
        }
        LocalDateTime resetCodeExpiration = user.getResetCodeExpiration();
        if (resetCodeExpiration == null || resetCodeExpiration.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }


    public void updatePassword(String resetCode, String newPassword) {
        User user = userRepository.findByResetCode(resetCode);
        if (user != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            user.setResetCode(null);
            user.setResetCodeExpiration(null);
            userRepository.save(user);
        }
    }
    public boolean changeUsername(String currentUsername, String newUsername) {
        User user = userRepository.findByUsername(currentUsername);
        if (user != null && isUsernameAvailable(newUsername)) {
            user.setUsername(newUsername);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean changeEmail(String currentUsername, String newEmail) {
        User user = userRepository.findByUsername(currentUsername);
        if (user != null && isEmailAvailable(newEmail) && isEmailValid(newEmail)) {
            user.setEmail(newEmail);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean changePassword(String currentUsername, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(currentUsername);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword()) && isPasswordValid(newPassword)) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }


    public void updateBio(String username, String newBio) {
        User user = findByUsername(username);
        if (user != null) {
            user.setBio(newBio);
            userRepository.save(user);
        }
    }

    public void updateProfilePicture(String username, String newProfilePicture) {
        User user = findByUsername(username);
        if (user != null) {
            user.setProfilePicture(newProfilePicture);
            userRepository.save(user);
        }
    }
    public void uploadProfilePicture(String username, MultipartFile file) throws IOException {
        // Create the directory if it doesn't exist
        Files.createDirectories(Paths.get(FILE_STORAGE_LOCATION));

        // Copy the file to the target location (Replacing existing file with the same name)
        Path targetLocation = Paths.get(FILE_STORAGE_LOCATION).resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Update the user's profile picture with the filename
        updateProfilePicture(username, file.getOriginalFilename());
    }

    public Resource downloadProfilePicture(String fileName) throws MalformedURLException {
        // Load file as Resource
        Path filePath = Paths.get(FILE_STORAGE_LOCATION).resolve(fileName).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // If resource doesn't exist, load default profile picture
        if (resource == null || !resource.exists()) {
            Path defaultPath = Paths.get("src/main/resources/Images/Default_pfp.svg.png");
            resource = new UrlResource(defaultPath.toUri());
        }

        return resource;
    }


    public GameList createGameList(String username, String name) {
        User user = findByUsername(username);
        if (user != null) {
            GameList gameList = new GameList();
            gameList.setUserId(user.getId());
            gameList.setName(name);
            gameList.setGames(new ArrayList<>());
            gameListRepository.save(gameList);

            user.getGameLists().add(gameList.getId());
            userRepository.save(user);

            return gameList;
        }
        return null;
    }

    public GameList updateGameList(String username, String gameListId, String newName) {
        User user = findByUsername(username);
        if (user != null) {
            GameList gameList = gameListRepository.findById(gameListId).orElse(null);
            if (gameList != null && gameList.getUserId().equals(user.getId())) {
                gameList.setName(newName);
                gameListRepository.save(gameList);
                return gameList;
            }
        }
        return null;
    }

    public void deleteGameList(String username, String gameListId) {
        User user = findByUsername(username);
        if (user != null) {
            GameList gameList = gameListRepository.findById(gameListId).orElse(null);
            if (gameList != null && gameList.getUserId().equals(user.getId())) {
                user.getGameLists().remove(gameListId);
                userRepository.save(user);
                gameListRepository.deleteById(gameListId);
            }
        }
    }
    public boolean addGameToList(String userId, String gameListId, String gameId) {
        User user = findById(userId);
        if (user != null) {
            GameList gameList = gameListRepository.findById(gameListId).orElse(null);
            if (gameList != null && gameList.getUserId().equals(user.getId())) {
                if (gameList.getGames().contains(gameId)) {
                    return false; // Game is already in the list.
                } else {
                    gameList.getGames().add(gameId);
                    gameListRepository.save(gameList);
                    return true; // Game successfully added to the list.
                }
            }
        }
        return false; // Failed to add game to the list.
    }
    public boolean deleteGameFromList(String userId, String gameListId, String gameId) {
        User user = findById(userId);
        if (user != null) {
            GameList gameList = gameListRepository.findById(gameListId).orElse(null);
            if (gameList != null && gameList.getUserId().equals(user.getId())) {
                if (gameList.getGames().contains(gameId)) {
                    gameList.getGames().remove(gameId);
                    gameListRepository.save(gameList);
                    return true;
                }
            }
        }
        return false;
    }
    public List<GameList> findGameListsByUser(User user) {
        // Fetch game lists by user
        // This might differ based on your repository and database implementation
        return gameListRepository.findByUserId(user.getId());
    }
    public List<String> getAllUsernames() {
        return userRepository.findAll()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getGameListById(String id) {
        GameList gameList = gameListRepository.findById(id).orElse(null);
        Map<String, Object> gameListInfo = new HashMap<>();

        if (gameList != null) {
            String gameListId = gameList.getId();
            String userId = gameList.getUserId();
            String gameListName = gameList.getName();
            gameListInfo.put("gameListId", gameListId);
            gameListInfo.put("userId", userId);
            gameListInfo.put("gameListName", gameListName);

            List<String> gameIds = gameList.getGames();
            if (gameIds != null) {
                // Fetch all games whose IDs are in the gameIds list in one call
                List<Game> games = gameRepository.findAllById(gameIds);
                // Create a map from game ID to game
                Map<String, Game> gameMap = games.stream()
                        .collect(Collectors.toMap(Game::getId, Function.identity()));

                // Build game info list with IDs and names
                List<Map<String, String>> gameInfoList = new ArrayList<>();
                for (String gameId : gameIds) {
                    Game game = gameMap.get(gameId);
                    if (game != null) {
                        Map<String, String> gameInfo = new HashMap<>();
                        gameInfo.put("gameId", gameId);
                        gameInfo.put("gameName", game.getName());
                        gameInfoList.add(gameInfo);
                    }
                }
                gameListInfo.put("games", gameInfoList);
            }
        }
        return gameListInfo;
    }

    public List<GameList> getAllUsersLists() {
        return gameListRepository.findAll();
    }
}
