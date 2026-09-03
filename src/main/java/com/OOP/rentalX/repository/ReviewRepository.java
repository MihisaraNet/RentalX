package com.OOP.rentalX.repository;

import com.OOP.rentalX.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByVehicleId(String vehicleId);
    List<Review> findByUserId(String userId);
}
