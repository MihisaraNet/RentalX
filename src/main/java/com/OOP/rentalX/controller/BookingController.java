package com.OOP.rentalX.controller;

import com.OOP.rentalX.model.Booking;
import com.OOP.rentalX.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service = new BookingService();

    @PostMapping("/add")
    public ResponseEntity<String> addBooking(@RequestBody Booking b) {
        try {
            service.addBooking(b);
            return ResponseEntity.ok("Booking added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/all")
    public List<Booking> getAll() {
        return service.getAllBookings();
    }

    @GetMapping("/user/{id}")
    public List<Booking> getUserBookings(@PathVariable String id) {
        return service.getAllBookings().stream()
                .filter(b -> b.getUserId().equals(id))
                .collect(Collectors.toList());
    }

    @PutMapping("/update/{id}")
    public String update(@PathVariable String id, @RequestBody Booking updated) {
        service.updateBooking(id, updated);
        return "Booking updated!";
    }

    @GetMapping("/driver/{id}")
    public List<Booking> getBookingsForDriver(@PathVariable String id) {
        return service.getAllBookings().stream()
                .filter(b -> b.getDriverId().equals(id))
                .toList();
    }

    @PutMapping("/approve/{id}")
    public String approve(@PathVariable String id) {
        return service.updateStatus(id, "Approved");
    }

    @PutMapping("/reject/{id}")
    public String reject(@PathVariable String id) {
        return service.updateStatus(id, "Rejected");
    }



    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        service.deleteBooking(id);
        return "Booking deleted!";
    }
}
