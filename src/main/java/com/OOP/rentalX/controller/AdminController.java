package com.OOP.rentalX.controller;

import com.OOP.rentalX.model.Admin;
import com.OOP.rentalX.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service = new AdminService();

    @PostMapping("/login")
    public ResponseEntity<Admin> login(@RequestParam String adminId, @RequestParam String password) {
        Admin admin = service.login(adminId, password);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(admin);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Admin admin) {
        if (admin.getAdminId() == null || admin.getAdminId().trim().isEmpty() ||
            admin.getName() == null || admin.getName().trim().isEmpty() ||
            admin.getEmail() == null || admin.getEmail().trim().isEmpty() ||
            admin.getPassword() == null || admin.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("All fields are required.");
        }
        if (admin.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters long.");
        }
        boolean duplicate = service.getAll().stream()
                .anyMatch(a -> a.getAdminId().equalsIgnoreCase(admin.getAdminId().trim()));
        if (duplicate) {
            return ResponseEntity.badRequest().body("Admin ID already exists.");
        }
        service.register(admin);
        return ResponseEntity.ok("Admin registered.");
    }

    @GetMapping("/all")
    public List<Admin> getAllAdmins() {
        return service.getAll();
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAdmin(@PathVariable String id) {
        return service.deleteAdmin(id) ? "Deleted" : "Admin not found";
    }

    @PutMapping("/update")
    public String updateAdmin(@RequestBody Admin updatedAdmin) {
        return service.updateAdmin(updatedAdmin) ? "Updated" : "Admin not found";
    }

}
