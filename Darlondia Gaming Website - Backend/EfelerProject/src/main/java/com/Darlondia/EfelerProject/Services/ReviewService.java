    package com.Darlondia.EfelerProject.Services;

    import com.Darlondia.EfelerProject.Models.Game;
    import com.Darlondia.EfelerProject.Models.Review;
    import com.Darlondia.EfelerProject.Models.User;
    import com.Darlondia.EfelerProject.Repositories.GameRepository;
    import com.Darlondia.EfelerProject.Repositories.ReviewRepository;
    import org.bson.types.ObjectId;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;
    import com.Darlondia.EfelerProject.Models.User;
    import com.Darlondia.EfelerProject.Repositories.UserRepository;

    @Service
    public class ReviewService {

        private final ReviewRepository reviewRepository;
        private final UserRepository userRepository;
        private final GameRepository gameRepository;
        //private final GameService gameService;

        @Autowired
        public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, GameRepository gameRepository) {
            this.reviewRepository = reviewRepository;
            this.userRepository = userRepository;
            this.gameRepository = gameRepository;
        }



        public Review createReview(String gameId, String userId, String content, int rating) {
            Review review = new Review();
            review.setUserId(userId);
            review.setGameId(gameId);
            review.setContent(content);
            review.setRating(rating);
            review.setCreatedAt(LocalDateTime.now());

            // Fetch the username from the UserRepository
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                review.setUsername(user.getUsername());
            }

            // Fetch the game name from the GameRepository
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game != null) {
                review.setGameName(game.getName());
            }

            return reviewRepository.save(review);
        }

        // Add a new method to get reviews by gameId
        public List<Review> getReviewsByGameId(String gameId) {
            return reviewRepository.findByGameId(gameId);
        }

        public Optional<Review> updateReview(String reviewId, String userId, String content, int rating) {
            return reviewRepository.findByIdAndUserId(reviewId, userId)
                    .map(review -> {
                        review.setContent(content);
                        review.setRating(rating);
                        return reviewRepository.save(review);
                    });
        }


        public void deleteReview(String reviewId, String userId) {
            reviewRepository.deleteByIdAndUserId(reviewId, userId);
        }
        public List<Review> findReviewsByUser(User user) {
            // Fetch reviews by user
            List<Review> reviews = reviewRepository.findByUserId(user.getId());
            return reviews;
        }

    }
