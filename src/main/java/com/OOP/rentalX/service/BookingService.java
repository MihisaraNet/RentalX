package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Booking;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private static final String FILE_PATH = "src/main/resources/bookings.txt";

    public void addBooking(Booking newBooking) {
        List<Booking> bookings = getAllBookings();

        for (Booking b : bookings) {
            if (b.getVehicleId().equals(newBooking.getVehicleId()) &&
                    b.getStatus().equalsIgnoreCase("Approved") &&
                    datesOverlap(b.getBookingDate(), b.getReturnDate(),
                            newBooking.getBookingDate(), newBooking.getReturnDate())) {

                System.out.println("Overlap detected: " + b.getBookingId());
                throw new IllegalArgumentException("This vehicle is already approved for a booking during the selected period.");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toLine(newBooking));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write booking to file.");
        }
    }


    private boolean datesOverlap(String start1, String end1, String start2, String end2) {
        LocalDate s1 = LocalDate.parse(start1);
        LocalDate e1 = LocalDate.parse(end1);
        LocalDate s2 = LocalDate.parse(start2);
        LocalDate e2 = LocalDate.parse(end2);
        return !(e1.isBefore(s2) || s1.isAfter(e2));
    }


    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(fromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateBooking(String id, Booking updated) {
        List<Booking> list = getAllBookings();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Booking b : list) {
                if (b.getBookingId().equals(id)) {
                    writer.write(toLine(updated));
                } else {
                    writer.write(toLine(b));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String updateStatus(String bookingId, String newStatus) {
        List<Booking> bookings = getAllBookings();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Booking b : bookings) {
                if (b.getBookingId().equals(bookingId)) {
                    b.setStatus(newStatus);
                }
                writer.write(toLine(b));
                writer.newLine();
            }
            return "Status updated to " + newStatus;
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to update status";
        }
    }


    public void deleteBooking(String id) {
        List<Booking> list = getAllBookings();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Booking b : list) {
                if (!b.getBookingId().equals(id)) {
                    writer.write(toLine(b));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toLine(Booking b) {
        return String.join(",", b.getBookingId(), b.getUserId(), b.getVehicleId(), b.getDriverId(),
                b.getBookingDate(), b.getReturnDate(), b.getStatus());
    }

    private Booking fromLine(String line) {
        String[] p = line.split(",", 7);
        return new Booking(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
    }
}
