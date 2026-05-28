package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Vehicle;
import com.OOP.rentalX.util.SelectionSortUtil;
import com.OOP.rentalX.util.RentedVehicleList;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class VehicleService {

    private static final String FILE_PATH = "src/main/resources/vehicles.txt";
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";
    private final RentedVehicleList rentedVehicles = new RentedVehicleList();

    public VehicleService() {
        List<Vehicle> all = loadVehicles();
        for (Vehicle v : all) {
            if (!v.isAvailable()) {
                rentedVehicles.add(v);
            }
        }
    }

    public void addVehicle(Vehicle v, MultipartFile imageFile) {
        List<Vehicle> existingVehicles = loadVehicles();

        
        for (Vehicle vehicle : existingVehicles) {
            if (vehicle.getVehicleId().equalsIgnoreCase(v.getVehicleId())) {
                throw new IllegalArgumentException("Vehicle ID already exists.");
            }
        }

        try {
            // Image upload logic remains the same
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = imageFile.getOriginalFilename();
            if (fileName != null && !fileName.isEmpty()) {
                String uniqueFileName = UUID.randomUUID() + "_" + fileName;
                Path imagePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                Files.copy(imageFile.getInputStream(), imagePath);
                v.setImagePath("/uploads/" + uniqueFileName);
            }

            // Save new vehicle
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(toLine(v));
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateVehicle(String id, Vehicle updated, MultipartFile imageFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = imageFile.getOriginalFilename();
                if (fileName != null && !fileName.isEmpty()) {
                    String uniqueFileName = UUID.randomUUID() + "_" + fileName;
                    Path imagePath = Paths.get(UPLOAD_DIR + uniqueFileName);
                    Files.copy(imageFile.getInputStream(), imagePath);
                    updated.setImagePath("/uploads/" + uniqueFileName);
                }
            }

            List<Vehicle> vehicles = loadVehicles();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Vehicle v : vehicles) {
                    if (v.getVehicleId().equals(id)) {
                        writer.write(toLine(updated));
                    } else {
                        writer.write(toLine(v));
                    }
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteVehicle(String id) {
        List<Vehicle> vehicles = loadVehicles();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Vehicle v : vehicles) {
                if (!v.getVehicleId().equals(id)) {
                    writer.write(toLine(v));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //remove a vehicle from the linked list
    //calls the delete method from the linked list
    public boolean removeFromRentedList(String vehicleId) {
        boolean removed = rentedVehicles.delete(vehicleId);
        if (removed) {
            List<Vehicle> all = loadVehicles();
            for (Vehicle v : all) {
                if (v.getVehicleId().equals(vehicleId)) {
                    v.setAvailable(true); // make available again
                    break;
                }
            }
            saveAllVehicles(all);
        }
        return removed;
    }


    public List<Vehicle> getRentedVehicleList() {
        return rentedVehicles.toList();
    }


    public List<Vehicle> getAllVehicles(boolean sortByPrice) {
        List<Vehicle> vehicles = loadVehicles();
        if (sortByPrice) {
            SelectionSortUtil.sortByRentPrice(vehicles);
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByDriverId(String driverId) {
        List<Vehicle> all = getAllVehicles(false);
        List<Vehicle> filtered = new ArrayList<>();
        for (Vehicle v : all) {
            if (v.getDriverId().equals(driverId)) {
                filtered.add(v);
            }
        }
        return filtered;
    }

    public void rentVehicle(String vehicleId) {
        List<Vehicle> vehicles = loadVehicles();
        boolean updated = false;
        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equals(vehicleId) && v.isAvailable()) {
                v.setAvailable(false); // mark as rented
                rentedVehicles.add(v); // store in linked list
                updated = true;
                break;
            }
        }
        if (updated) {
            saveAllVehicles(vehicles);
            System.out.println("Vehicle rented and added to linked list.");
        } else {
            System.out.println("Vehicle not found or already rented.");
        }
    }

    public void printAllRentedVehicles() {
        rentedVehicles.printAll();
    }

    private List<Vehicle> loadVehicles() {
        List<Vehicle> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Vehicle v = fromLine(line);
                if (v != null) list.add(v);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveAllVehicles(List<Vehicle> vehicles) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Vehicle v : vehicles) {
                writer.write(toLine(v));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean toggleAvailability(String vehicleId) {
        List<Vehicle> vehicles = loadVehicles();
        boolean found = false;

        for (Vehicle v : vehicles) {
            if (v.getVehicleId().equals(vehicleId)) {
                v.setAvailable(!v.isAvailable());
                found = true;
                break;
            }
        }

        if (found) {
            saveAllVehicles(vehicles);
        }

        return found;
    }


    private String toLine(Vehicle v) {
        return String.join(",", v.getVehicleId(), v.getModel(), v.getType(),
                String.valueOf(v.isAvailable()), String.valueOf(v.getRentPrice()),
                v.getImagePath() != null ? v.getImagePath() : "", v.getDriverId());
    }

    private Vehicle fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",", 7);
        if (parts.length < 7) return null;
        return new Vehicle(
                parts[0], parts[1], parts[2],
                Boolean.parseBoolean(parts[3]),
                Double.parseDouble(parts[4]),
                parts[5],
                parts[6]
        );
    }
}
