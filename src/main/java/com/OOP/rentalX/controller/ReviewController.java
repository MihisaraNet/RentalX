package com.OOP.rentalX.controller;

import com.OOP.rentalX.dto.ApiResponse;
import com.OOP.rentalX.model.Review;
import com.OOP.rentalX.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> addReview(@RequestBody Review review) {
        reviewService.addReview(review);
        return ResponseEntity.ok(ApiResponse.ok("Review added successfully!", review.getReviewId()));
    }

    @GetMapping("/all")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<Review> getReviewsByVehicle(@PathVariable String vehicleId) {
        return reviewService.getReviewsByVehicleId(vehicleId);
    }

    @GetMapping("/user/{userId}")
    public List<Review> getReviewsByUser(@PathVariable String userId) {
        return reviewService.getReviewsByUserId(userId);
    }

    @PutMapping("/update/{id}")
    public String updateReview(@PathVariable String id, @RequestBody Review review) {
        reviewService.updateReview(id, review);
        return "Review updated successfully!";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return "Review deleted successfully!";
    }
}
