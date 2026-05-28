package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Review;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {

    private static final String FILE_PATH = "src/main/resources/reviews.txt";

    public void addReview(Review review) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toLine(review));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Review> getAllReviews() {
        List<Review> reviews = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                reviews.add(fromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public void updateReview(String reviewId, Review updatedReview) {
        List<Review> reviews = getAllReviews();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Review review : reviews) {
                if (review.getReviewId().equals(reviewId)) {
                    writer.write(toLine(updatedReview));
                } else {
                    writer.write(toLine(review));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteReview(String reviewId) {
        List<Review> reviews = getAllReviews();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Review review : reviews) {
                if (!review.getReviewId().equals(reviewId)) {
                    writer.write(toLine(review));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toLine(Review r) {
        return r.getReviewId() + "," + r.getUserId() + "," + r.getVehicleId() + "," + r.getReviewText().replace(",", ";") + "," + r.getRating();
    }

    private Review fromLine(String line) {
        String[] parts = line.split(",", 5);
        return new Review(parts[0], parts[1], parts[2], parts[3].replace(";", ","), Integer.parseInt(parts[4]));
    }
}
