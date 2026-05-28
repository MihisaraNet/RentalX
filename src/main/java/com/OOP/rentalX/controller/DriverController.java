package com.OOP.rentalX.controller;

import com.OOP.rentalX.model.Driver;
import com.OOP.rentalX.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
public class DriverController {

    private final DriverService service = new DriverService();

    @PostMapping("/register")
    public String register(@RequestBody Driver driver) {
        service.addDriver(driver);
        return "Driver registered!";
    }

    @PostMapping("/login")
    public ResponseEntity<Driver> login(@RequestParam String driverId, @RequestParam String password) {
        return service.getAllDrivers().stream()
                .filter(d -> d.getDriverId().equals(driverId) && d.getPassword().equals(password))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @GetMapping("/profile/{id}")
    public Driver getProfile(@PathVariable String id) {
        return service.getAllDrivers().stream()
                .filter(d -> d.getDriverId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PutMapping("/update")
    public String update(@RequestBody Driver updated) {
        service.updateDriver(updated);
        return "Driver profile updated!";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        service.deleteDriver(id);
        return "Driver deleted!";
    }

    @GetMapping("/all")
    public List<Driver> all() {
        return service.getAllDrivers();
    }
}
