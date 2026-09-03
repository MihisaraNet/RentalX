package com.OOP.rentalX.repository;

import com.OOP.rentalX.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByUserId(String userId);
    List<Booking> findByDriverId(String driverId);
    List<Booking> findByVehicleId(String vehicleId);

    @Query("SELECT b FROM Booking b WHERE b.vehicleId = :vehicleId AND b.status NOT IN ('Cancelled', 'Rejected')")
    List<Booking> findActiveBookingsByVehicle(@Param("vehicleId") String vehicleId);
}
