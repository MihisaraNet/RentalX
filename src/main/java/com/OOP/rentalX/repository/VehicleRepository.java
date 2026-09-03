package com.OOP.rentalX.repository;

import com.OOP.rentalX.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByDriverId(String driverId);
    List<Vehicle> findByAvailable(boolean available);
    List<Vehicle> findByTypeIgnoreCase(String type);
}
