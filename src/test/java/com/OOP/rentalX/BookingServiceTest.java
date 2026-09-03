package com.OOP.rentalX;

import com.OOP.rentalX.model.Booking;
import com.OOP.rentalX.model.Vehicle;
import com.OOP.rentalX.repository.BookingRepository;
import com.OOP.rentalX.repository.VehicleRepository;
import com.OOP.rentalX.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BookingServiceTest {

    private BookingRepository bookingRepository;
    private VehicleRepository vehicleRepository;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        vehicleRepository = Mockito.mock(VehicleRepository.class);
        bookingService = new BookingService(bookingRepository, vehicleRepository);
    }

    @Test
    @DisplayName("Booking calculation accurately calculates rental cost based on day count")
    void testBookingCostCalculation() {
        Vehicle vehicle = new Vehicle("V001", "Tesla Model 3", "Electric", true, 100.0, "", "DRV001");
        when(vehicleRepository.findById("V001")).thenReturn(Optional.of(vehicle));
        when(bookingRepository.findActiveBookingsByVehicle("V001")).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = new Booking();
        booking.setUserId("user1");
        booking.setVehicleId("V001");
        booking.setBookingDate("2026-10-01");
        booking.setReturnDate("2026-10-05"); // 4 days

        Booking result = bookingService.addBooking(booking);

        assertNotNull(result.getBookingId());
        assertEquals(400.0, result.getTotalCost());
        assertEquals("Pending", result.getStatus());
    }

    @Test
    @DisplayName("BookingService throws IllegalArgumentException when dates overlap with existing approved booking")
    void testBookingOverlapPrevention() {
        Vehicle vehicle = new Vehicle("V001", "Tesla Model 3", "Electric", true, 100.0, "", "DRV001");
        when(vehicleRepository.findById("V001")).thenReturn(Optional.of(vehicle));

        Booking existingApproved = new Booking("BK-01", "otherUser", "V001", "DRV001", "2026-10-01", "2026-10-05", "Approved");
        when(bookingRepository.findActiveBookingsByVehicle("V001")).thenReturn(List.of(existingApproved));

        Booking conflictingBooking = new Booking();
        conflictingBooking.setUserId("user1");
        conflictingBooking.setVehicleId("V001");
        conflictingBooking.setBookingDate("2026-10-03");
        conflictingBooking.setReturnDate("2026-10-07");

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.addBooking(conflictingBooking);
        });
    }
}
