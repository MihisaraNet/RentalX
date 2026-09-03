package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Booking;
import com.OOP.rentalX.model.Vehicle;
import com.OOP.rentalX.repository.BookingRepository;
import com.OOP.rentalX.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;

    public BookingService(BookingRepository bookingRepository, VehicleRepository vehicleRepository) {
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public Booking addBooking(Booking newBooking) {
        if (newBooking.getBookingId() == null || newBooking.getBookingId().trim().isEmpty()) {
            newBooking.setBookingId("BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Validate vehicle existence
        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(newBooking.getVehicleId());
        if (vehicleOpt.isEmpty()) {
            throw new IllegalArgumentException("Vehicle not found: " + newBooking.getVehicleId());
        }
        Vehicle vehicle = vehicleOpt.get();

        // Assign vehicle driver if not explicitly passed
        if ((newBooking.getDriverId() == null || newBooking.getDriverId().trim().isEmpty()) && vehicle.getDriverId() != null) {
            newBooking.setDriverId(vehicle.getDriverId());
        }

        // Validate dates & calculate total cost
        try {
            LocalDate startDate = LocalDate.parse(newBooking.getBookingDate());
            LocalDate returnDate = LocalDate.parse(newBooking.getReturnDate());

            if (returnDate.isBefore(startDate)) {
                throw new IllegalArgumentException("Return date cannot be earlier than booking start date.");
            }

            long days = ChronoUnit.DAYS.between(startDate, returnDate);
            if (days <= 0) days = 1; // Minimum 1 day

            if (newBooking.getTotalCost() == null || newBooking.getTotalCost() <= 0) {
                newBooking.setTotalCost(days * vehicle.getRentPrice());
            }
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) throw e;
            // Ignore parse errors if date is non-standard, but preserve operation
        }

        // Overlap detection against approved bookings
        List<Booking> activeBookings = bookingRepository.findActiveBookingsByVehicle(newBooking.getVehicleId());
        for (Booking b : activeBookings) {
            if ("Approved".equalsIgnoreCase(b.getStatus()) || "Active".equalsIgnoreCase(b.getStatus())) {
                if (datesOverlap(b.getBookingDate(), b.getReturnDate(), newBooking.getBookingDate(), newBooking.getReturnDate())) {
                    throw new IllegalArgumentException("Vehicle is already booked and approved during the selected date range.");
                }
            }
        }

        if (newBooking.getStatus() == null || newBooking.getStatus().trim().isEmpty()) {
            newBooking.setStatus("Pending");
        }

        return bookingRepository.save(newBooking);
    }

    public boolean datesOverlap(String start1, String end1, String start2, String end2) {
        try {
            LocalDate s1 = LocalDate.parse(start1);
            LocalDate e1 = LocalDate.parse(end1);
            LocalDate s2 = LocalDate.parse(start2);
            LocalDate e2 = LocalDate.parse(end2);
            return !(e1.isBefore(s2) || s1.isAfter(e2));
        } catch (Exception e) {
            return false;
        }
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByDriver(String driverId) {
        return bookingRepository.findByDriverId(driverId);
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Transactional
    public void updateBooking(String id, Booking updated) {
        bookingRepository.findById(id).ifPresent(existing -> {
            existing.setUserId(updated.getUserId());
            existing.setVehicleId(updated.getVehicleId());
            existing.setDriverId(updated.getDriverId());
            existing.setBookingDate(updated.getBookingDate());
            existing.setReturnDate(updated.getReturnDate());
            existing.setStatus(updated.getStatus());
            if (updated.getTotalCost() != null) existing.setTotalCost(updated.getTotalCost());
            if (updated.getPaymentStatus() != null) existing.setPaymentStatus(updated.getPaymentStatus());
            bookingRepository.save(existing);
        });
    }

    @Transactional
    public String updateStatus(String bookingId, String newStatus) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isPresent()) {
            Booking b = opt.get();
            b.setStatus(newStatus);
            bookingRepository.save(b);

            // Synchronize vehicle availability if approved/completed/cancelled
            if ("Approved".equalsIgnoreCase(newStatus) || "Active".equalsIgnoreCase(newStatus)) {
                vehicleRepository.findById(b.getVehicleId()).ifPresent(v -> {
                    v.setAvailable(false);
                    vehicleRepository.save(v);
                });
            } else if ("Completed".equalsIgnoreCase(newStatus) || "Cancelled".equalsIgnoreCase(newStatus) || "Rejected".equalsIgnoreCase(newStatus)) {
                vehicleRepository.findById(b.getVehicleId()).ifPresent(v -> {
                    v.setAvailable(true);
                    vehicleRepository.save(v);
                });
            }
            return "Status updated to " + newStatus;
        }
        return "Booking not found";
    }

    @Transactional
    public void deleteBooking(String id) {
        bookingRepository.deleteById(id);
    }
}
