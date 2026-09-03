package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Vehicle;
import com.OOP.rentalX.repository.VehicleRepository;
import com.OOP.rentalX.util.RentedVehicleList;
import com.OOP.rentalX.util.SelectionSortUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VehicleService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private final VehicleRepository vehicleRepository;
    private final RentedVehicleList rentedVehicles = new RentedVehicleList();

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @PostConstruct
    public void initRentedVehicles() {
        List<Vehicle> all = vehicleRepository.findAll();
        for (Vehicle v : all) {
            if (!v.isAvailable()) {
                rentedVehicles.add(v);
            }
        }
    }

    public void addVehicle(Vehicle v, MultipartFile imageFile) {
        if (vehicleRepository.existsById(v.getVehicleId())) {
            throw new IllegalArgumentException("Vehicle ID already exists.");
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = imageFile.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID() + "_" + (fileName != null ? fileName : "image.jpg");
                Path imagePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.copy(imageFile.getInputStream(), imagePath);
                v.setImagePath("/uploads/" + uniqueFileName);
            } catch (IOException e) {
                System.err.println("Warning: Failed to save image to filesystem: " + e.getMessage());
            }
        }

        vehicleRepository.save(v);
    }

    @Transactional
    public void updateVehicle(String id, Vehicle updated, MultipartFile imageFile) {
        Optional<Vehicle> opt = vehicleRepository.findById(id);
        if (opt.isPresent()) {
            Vehicle existing = opt.get();
            existing.setModel(updated.getModel());
            existing.setType(updated.getType());
            existing.setAvailable(updated.isAvailable());
            existing.setRentPrice(updated.getRentPrice());
            existing.setDriverId(updated.getDriverId());

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    File uploadDir = new File(UPLOAD_DIR);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    String fileName = imageFile.getOriginalFilename();
                    String uniqueFileName = UUID.randomUUID() + "_" + (fileName != null ? fileName : "image.jpg");
                    Path imagePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                    Files.copy(imageFile.getInputStream(), imagePath);
                    existing.setImagePath("/uploads/" + uniqueFileName);
                } catch (IOException e) {
                    System.err.println("Warning: Failed to save image: " + e.getMessage());
                }
            } else if (updated.getImagePath() != null && !updated.getImagePath().trim().isEmpty()) {
                existing.setImagePath(updated.getImagePath());
            }

            vehicleRepository.save(existing);
        }
    }

    @Transactional
    public void deleteVehicle(String id) {
        rentedVehicles.delete(id);
        vehicleRepository.deleteById(id);
    }

    public List<Vehicle> getAllVehicles(boolean sortByPrice) {
        List<Vehicle> vehicles = new ArrayList<>(vehicleRepository.findAll());
        if (sortByPrice) {
            SelectionSortUtil.sortByRentPrice(vehicles);
        }
        return vehicles;
    }

    public Optional<Vehicle> getVehicleById(String vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }

    public List<Vehicle> getVehiclesByDriverId(String driverId) {
        return vehicleRepository.findByDriverId(driverId);
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByAvailable(true);
    }

    @Transactional
    public void rentVehicle(String vehicleId) {
        vehicleRepository.findById(vehicleId).ifPresent(v -> {
            if (v.isAvailable()) {
                v.setAvailable(false);
                vehicleRepository.save(v);
                rentedVehicles.add(v);
            }
        });
    }

    @Transactional
    public boolean removeFromRentedList(String vehicleId) {
        boolean removed = rentedVehicles.delete(vehicleId);
        vehicleRepository.findById(vehicleId).ifPresent(v -> {
            v.setAvailable(true);
            vehicleRepository.save(v);
        });
        return removed;
    }

    public List<Vehicle> getRentedVehicleList() {
        return rentedVehicles.toList();
    }

    public void printAllRentedVehicles() {
        rentedVehicles.printAll();
    }

    @Transactional
    public boolean toggleAvailability(String vehicleId) {
        Optional<Vehicle> opt = vehicleRepository.findById(vehicleId);
        if (opt.isPresent()) {
            Vehicle v = opt.get();
            boolean newAvailability = !v.isAvailable();
            v.setAvailable(newAvailability);
            vehicleRepository.save(v);
            if (newAvailability) {
                rentedVehicles.delete(vehicleId);
            } else {
                rentedVehicles.add(v);
            }
            return true;
        }
        return false;
    }
}
