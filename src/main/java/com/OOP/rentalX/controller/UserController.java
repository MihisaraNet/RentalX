package com.OOP.rentalX.controller;

import com.OOP.rentalX.model.User;
import com.OOP.rentalX.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service = new UserService();

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        if (user.getUserId() == null || user.getUserId().trim().isEmpty() ||
            user.getName() == null || user.getName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPhone() == null || user.getPhone().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("All fields are required.");
        }
        if (service.getProfile(user.getUserId()) != null) {
            return ResponseEntity.badRequest().body("User ID already exists.");
        }
        service.register(user);
        return ResponseEntity.ok("User registered!");
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String userId, @RequestParam String password) {
        User user = service.login(userId, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile/{id}")
    public User profile(@PathVariable String id) {
        return service.getProfile(id);
    }

    @PutMapping("/update")
    public String update(@RequestBody User user) {
        service.updateProfile(user);
        return "User updated!";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        service.deleteProfile(id);
        return "User deleted!";
    }
}
