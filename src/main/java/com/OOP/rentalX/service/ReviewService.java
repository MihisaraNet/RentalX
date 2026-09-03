package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Review;
import com.OOP.rentalX.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void addReview(Review review) {
        if (review.getReviewId() == null || review.getReviewId().trim().isEmpty()) {
            review.setReviewId("REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        reviewRepository.save(review);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByVehicleId(String vehicleId) {
        return reviewRepository.findByVehicleId(vehicleId);
    }

    public List<Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Optional<Review> getReviewById(String reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Transactional
    public void updateReview(String reviewId, Review updatedReview) {
        reviewRepository.findById(reviewId).ifPresent(existing -> {
            existing.setUserId(updatedReview.getUserId());
            existing.setVehicleId(updatedReview.getVehicleId());
            existing.setReviewText(updatedReview.getReviewText());
            existing.setRating(updatedReview.getRating());
            reviewRepository.save(existing);
        });
    }

    @Transactional
    public void deleteReview(String reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
