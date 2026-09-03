package com.OOP.rentalX.controller;

import com.OOP.rentalX.dto.ApiResponse;
import com.OOP.rentalX.dto.BookingRequest;
import com.OOP.rentalX.model.Booking;
import com.OOP.rentalX.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Booking>> addBooking(@RequestBody BookingRequest request) {
        try {
            Booking b = new Booking();
            b.setBookingId(request.getBookingId());
            b.setUserId(request.getUserId());
            b.setVehicleId(request.getVehicleId());
            b.setDriverId(request.getDriverId());
            b.setBookingDate(request.getBookingDate());
            b.setReturnDate(request.getReturnDate());
            b.setStatus(request.getStatus() != null ? request.getStatus() : "Pending");
            b.setTotalCost(request.getTotalCost());
            if (request.getPaymentMethod() != null) {
                b.setPaymentStatus("Paid via " + request.getPaymentMethod());
            }

            Booking saved = service.addBooking(b);
            return ResponseEntity.ok(ApiResponse.ok("Booking confirmed successfully!", saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public List<Booking> getAll() {
        return service.getAllBookings();
    }

    @GetMapping("/user/{id}")
    public List<Booking> getUserBookings(@PathVariable String id) {
        return service.getBookingsByUser(id);
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable String id, @RequestBody Booking updated) {
        service.updateBooking(id, updated);
        return "Booking updated!";
    }

    @GetMapping("/driver/{id}")
    public List<Booking> getBookingsForDriver(@PathVariable String id) {
        return service.getBookingsByDriver(id);
    }

    @PutMapping("/approve/{id}")
    public String approve(@PathVariable String id) {
        return service.updateStatus(id, "Approved");
    }

    @PutMapping("/reject/{id}")
    public String reject(@PathVariable String id) {
        return service.updateStatus(id, "Rejected");
    }

    @PutMapping("/complete/{id}")
    public String complete(@PathVariable String id) {
        return service.updateStatus(id, "Completed");
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        service.deleteBooking(id);
        return "Booking deleted!";
    }
}
