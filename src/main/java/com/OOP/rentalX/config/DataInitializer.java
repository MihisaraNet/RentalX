package com.OOP.rentalX.config;

import com.OOP.rentalX.model.*;
import com.OOP.rentalX.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           AdminRepository adminRepository,
                           DriverRepository driverRepository,
                           VehicleRepository vehicleRepository,
                           BookingRepository bookingRepository,
                           ReviewRepository reviewRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmins();
        seedDrivers();
        seedUsers();
        seedVehicles();
        seedBookings();
        seedReviews();
    }

    private void seedAdmins() {
        if (adminRepository.count() == 0) {
            importAdminsFromFile();
            if (adminRepository.count() == 0) {
                adminRepository.save(new Admin("admin1", "System Administrator", "admin@rentalx.com", passwordEncoder.encode("admin123")));
            }
        }
    }

    private void seedDrivers() {
        if (driverRepository.count() == 0) {
            importDriversFromFile();
            if (driverRepository.count() == 0) {
                driverRepository.save(new Driver("DRV001", "Kamal Perera", "B1234567", "+94771234567", "kamal@rentalx.com", passwordEncoder.encode("driver123")));
                driverRepository.save(new Driver("DRV002", "Sunil Silva", "B7654321", "+94779876543", "sunil@rentalx.com", passwordEncoder.encode("driver123")));
            }
        }
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            importUsersFromFile();
            if (userRepository.count() == 0) {
                userRepository.save(new User("user1", "John Doe", "john@example.com", "+94712345678", passwordEncoder.encode("user123")));
                userRepository.save(new User("user2", "Jane Smith", "jane@example.com", "+94787654321", passwordEncoder.encode("user123")));
            }
        }
    }

    private void seedVehicles() {
        if (vehicleRepository.count() == 0) {
            importVehiclesFromFile();
            if (vehicleRepository.count() == 0) {
                vehicleRepository.save(new Vehicle("V001", "Tesla Model 3", "Electric Sedan", true, 85.0, "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600", "DRV001"));
                vehicleRepository.save(new Vehicle("V002", "BMW 5 Series", "Luxury Sedan", true, 120.0, "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600", "DRV002"));
                vehicleRepository.save(new Vehicle("V003", "Toyota Land Cruiser", "SUV", true, 150.0, "https://images.unsplash.com/photo-1594502184342-2e12f877aa73?w=600", "DRV001"));
                vehicleRepository.save(new Vehicle("V004", "Mercedes-Benz C-Class", "Luxury Sedan", true, 110.0, "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=600", "DRV002"));
                vehicleRepository.save(new Vehicle("V005", "Ford Mustang GT", "Sports Coupe", true, 140.0, "https://images.unsplash.com/photo-1584345604476-8ec5e12e42dd?w=600", "DRV001"));
            }
        }
    }

    private void seedBookings() {
        if (bookingRepository.count() == 0) {
            importBookingsFromFile();
        }
    }

    private void seedReviews() {
        if (reviewRepository.count() == 0) {
            importReviewsFromFile();
            if (reviewRepository.count() == 0) {
                reviewRepository.save(new Review("REV001", "user1", "V001", "Super smooth ride! Autopilot worked flawlessly.", 5));
                reviewRepository.save(new Review("REV002", "user2", "V002", "Very comfortable for long drives with family.", 4));
            }
        }
    }

    private void importAdminsFromFile() {
        File file = new File("src/main/resources/admins.txt");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 4);
                if (parts.length >= 4) {
                    adminRepository.save(new Admin(parts[0].trim(), parts[1].trim(), parts[2].trim(), passwordEncoder.encode(parts[3].trim())));
                }
            }
        } catch (Exception ignored) {}
    }

    private void importDriversFromFile() {
        File file = new File("src/main/resources/drivers.txt");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 6);
                if (parts.length >= 6) {
                    driverRepository.save(new Driver(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim(), passwordEncoder.encode(parts[5].trim())));
                }
            }
        } catch (Exception ignored) {}
    }

    private void importUsersFromFile() {
        File file = new File("src/main/resources/users.txt");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length >= 5) {
                    userRepository.save(new User(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), passwordEncoder.encode(parts[4].trim())));
                }
            }
        } catch (Exception ignored) {}
    }

    private void importVehiclesFromFile() {
        File file = new File("src/main/resources/vehicles.txt");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 7);
                if (parts.length >= 7) {
                    vehicleRepository.save(new Vehicle(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            Boolean.parseBoolean(parts[3].trim()),
                            Double.parseDouble(parts[4].trim()),
                            parts[5].trim(),
                            parts[6].trim()
                    ));
                }
            }
        } catch (Exception ignored) {}
    }

    private void importBookingsFromFile() {
        File file = new File("src/main/resources/bookings.txt");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 7);
                if (parts.length >= 7) {
                    bookingRepository.save(new Booking(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim(),
                            parts[6].trim()
                    ));
                }
            }
        } catch (Exception ignored) {}
    }

    private void importReviewsFromFile() {
        File file = new File("src/main/resources/reviews.txt");
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 5);
                if (parts.length >= 5) {
                    reviewRepository.save(new Review(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            Integer.parseInt(parts[4].trim())
                    ));
                }
            }
        } catch (Exception ignored) {}
    }
}
