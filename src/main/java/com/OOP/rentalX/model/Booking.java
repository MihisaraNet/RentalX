package com.OOP.rentalX.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(name = "booking_id", length = 50, nullable = false, unique = true)
    private String bookingId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "vehicle_id", length = 50, nullable = false)
    private String vehicleId;

    @Column(name = "driver_id", length = 50)
    private String driverId;

    @Column(name = "booking_date", length = 50, nullable = false)
    private String bookingDate;

    @Column(name = "return_date", length = 50, nullable = false)
    private String returnDate;

    @Column(nullable = false, length = 50)
    private String status = "Pending";

    @Column(name = "total_cost")
    private Double totalCost = 0.0;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus = "Unpaid";

    public Booking() {}

    public Booking(String bookingId, String userId, String vehicleId, String driverId,
                   String bookingDate, String returnDate, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.bookingDate = bookingDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public Booking(String bookingId, String userId, String vehicleId, String driverId,
                   String bookingDate, String returnDate, String status, Double totalCost, String paymentStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.bookingDate = bookingDate;
        this.returnDate = returnDate;
        this.status = status;
        this.totalCost = totalCost;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
