package com.Darlondia.EfelerProject.Repositories;

import com.Darlondia.EfelerProject.Models.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByGameId(String gameId);

    Optional<Review> findByIdAndUserId(String id, String userId);
    void deleteByIdAndUserId(String id, String userId);

    // Add the findByUserId method
    List<Review> findByUserId(String userId);
}
