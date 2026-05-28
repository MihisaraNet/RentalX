package com.OOP.rentalX.model;

public class Vehicle {
    private String vehicleId;
    private String model;
    private String type;
    private String driverId;
    private boolean available;
    private double rentPrice;
    private String imagePath;

    public Vehicle() {}

    public Vehicle(String vehicleId, String model, String type, boolean available, double rentPrice,String imagePath, String driverId) {
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
