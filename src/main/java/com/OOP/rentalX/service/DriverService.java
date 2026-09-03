package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Driver;
import com.OOP.rentalX.repository.DriverRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    public DriverService(DriverRepository driverRepository, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void addDriver(Driver d) {
        if (!d.getPassword().startsWith("$2a$")) {
            d.setPassword(passwordEncoder.encode(d.getPassword()));
        }
        driverRepository.save(d);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Optional<Driver> getDriverById(String driverId) {
        return driverRepository.findById(driverId);
    }

    @Transactional
    public void updateDriver(Driver updated) {
        driverRepository.findById(updated.getDriverId()).ifPresent(existing -> {
            existing.setName(updated.getName());
            existing.setLicenseNumber(updated.getLicenseNumber());
            existing.setPhone(updated.getPhone());
            existing.setEmail(updated.getEmail());
            if (updated.getPassword() != null && !updated.getPassword().trim().isEmpty()) {
                if (!updated.getPassword().equals(existing.getPassword())) {
                    existing.setPassword(passwordEncoder.encode(updated.getPassword()));
                }
            }
            driverRepository.save(existing);
        });
    }

    @Transactional
    public void deleteDriver(String driverId) {
        driverRepository.deleteById(driverId);
    }
}
