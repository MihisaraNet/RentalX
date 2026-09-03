package com.OOP.rentalX.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @Column(name = "review_id", length = 50, nullable = false, unique = true)
    private String reviewId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "vehicle_id", length = 50, nullable = false)
    private String vehicleId;

    @Column(name = "review_text", length = 1000)
    private String reviewText;

    @Column(nullable = false)
    private int rating;

    public Review() {
    }

    public Review(String reviewId, String userId, String vehicleId, String reviewText, int rating) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", userId='" + userId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", reviewText='" + reviewText + '\'' +
                ", rating=" + rating +
                '}';
    }
}
