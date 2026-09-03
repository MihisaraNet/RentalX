package com.OOP.rentalX.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @Column(name = "vehicle_id", length = 50, nullable = false, unique = true)
    private String vehicleId;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(name = "driver_id", length = 50)
    private String driverId;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "rent_price", nullable = false)
    private double rentPrice;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    public Vehicle() {}

    public Vehicle(String vehicleId, String model, String type, boolean available, double rentPrice, String imagePath, String driverId) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.type = type;
        this.available = available;
        this.rentPrice = rentPrice;
        this.imagePath = imagePath;
        this.driverId = driverId;
    }

    // Getters and Setters
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public double getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(double rentPrice) {
        this.rentPrice = rentPrice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
