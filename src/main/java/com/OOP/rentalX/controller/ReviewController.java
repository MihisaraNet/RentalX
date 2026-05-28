package com.OOP.rentalX.controller;

import com.OOP.rentalX.model.Review;
import com.OOP.rentalX.service.ReviewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService = new ReviewService();

    @PostMapping("/add")
    public String addReview(@RequestBody Review review) {
        reviewService.addReview(review);
        return "Review added successfully!";
    }

    @GetMapping("/all")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
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
