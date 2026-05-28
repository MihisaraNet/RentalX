package com.OOP.rentalX.controller;

import com.OOP.rentalX.model.Vehicle;
import com.OOP.rentalX.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService = new VehicleService();

    @PostMapping("/add")
    public ResponseEntity<String> addVehicle(@RequestParam("vehicleId") String vehicleId,
                                             @RequestParam("model") String model,
                                             @RequestParam("type") String type,
                                             @RequestParam("available") boolean available,
                                             @RequestParam("rentPrice") double rentPrice,
                                             @RequestParam("driverId") String driverId,
                                             @RequestParam("image") MultipartFile image) {
        try {
            Vehicle v = new Vehicle(vehicleId, model, type, available, rentPrice, null, driverId);
            vehicleService.addVehicle(v, image);
            return ResponseEntity.ok("Vehicle added successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/driver/{driverId}")
    public List<Vehicle> getByDriver(@PathVariable String driverId) {
        return vehicleService.getVehiclesByDriverId(driverId);
    }

    @GetMapping("/all")
    public List<Vehicle> getAllVehicles(@RequestParam(defaultValue = "false") boolean sortByPrice) {
        return vehicleService.getAllVehicles(sortByPrice);
    }

    @PutMapping("/update/{id}")
    public String updateVehicle(@PathVariable String id,
                                @RequestParam("model") String model,
                                @RequestParam("type") String type,
                                @RequestParam("available") boolean available,
                                @RequestParam("rentPrice") double rentPrice,
                                @RequestParam(value = "image", required = false) MultipartFile image,
                                @RequestParam("imagePath") String imagePath,
                                @RequestParam("driverId") String driverId) {
        Vehicle updatedVehicle = new Vehicle(id, model, type, available, rentPrice, imagePath, driverId);
        vehicleService.updateVehicle(id, updatedVehicle, image);
        return "Vehicle updated.";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return "Vehicle deleted.";
    }

    //endpoint for removing a vehicle from the linked list
    @DeleteMapping("/rented/{id}")
    public String removeRentedVehicle(@PathVariable String id) {
        return vehicleService.removeFromRentedList(id)
                ? "Vehicle removed from rented list and marked available."
                : "Vehicle not found in rented list.";
    }


    // ✅ Rent a vehicle and add to linked list
    @PostMapping("/rent/{id}")
    public String rentVehicle(@PathVariable String id) {
        vehicleService.rentVehicle(id);
        return "Vehicle rented (if available) and added to rented list.";
    }

    @PutMapping("/toggleAvailability/{id}")
    public String toggleAvailability(@PathVariable String id) {
        boolean result = vehicleService.toggleAvailability(id);
        return result ? "Vehicle availability updated." : "Vehicle not found.";
    }


    // ✅ Print all rented vehicles (debug/log)
    @GetMapping("/rented/print")
    public String printRentedVehicles() {
        vehicleService.printAllRentedVehicles();
        return "Printed all rented vehicles in server console.";
    }

    // ✅ Return all rented vehicles as JSON
    @GetMapping("/rented")
    public List<Vehicle> getAllRentedVehicles() {
        return vehicleService.getRentedVehicleList();
    }

}
