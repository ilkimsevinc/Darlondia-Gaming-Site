package com.Darlondia.EfelerProject.Controllers;

import com.Darlondia.EfelerProject.Models.Review;
import com.Darlondia.EfelerProject.Services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/game/{gameId}")
    public ResponseEntity<List<Review>> getReviewsByGameId(@PathVariable String gameId) {
        List<Review> reviews = reviewService.getReviewsByGameId(gameId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

}
