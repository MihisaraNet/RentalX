package com.OOP.rentalX.controller;

import com.OOP.rentalX.dto.ApiResponse;
import com.OOP.rentalX.dto.AuthRequest;
import com.OOP.rentalX.dto.AuthResponse;
import com.OOP.rentalX.dto.RegisterRequest;
import com.OOP.rentalX.model.Admin;
import com.OOP.rentalX.model.Driver;
import com.OOP.rentalX.model.User;
import com.OOP.rentalX.repository.AdminRepository;
import com.OOP.rentalX.repository.DriverRepository;
import com.OOP.rentalX.repository.UserRepository;
import com.OOP.rentalX.security.CustomUserDetailsService;
import com.OOP.rentalX.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(UserRepository userRepository,
                          AdminRepository adminRepository,
                          DriverRepository driverRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        String username = request.getUsername().trim();
        String password = request.getPassword();

        // 1. Try User
        Optional<User> userOpt = userRepository.findById(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword()) || password.equals(user.getPassword())) {
                // If password matched in plaintext, upgrade hash
                if (!user.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(password));
                    userRepository.save(user);
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String token = jwtService.generateToken(userDetails, "ROLE_USER");
                return ResponseEntity.ok(ApiResponse.ok("Login successful", new AuthResponse(token, user.getUserId(), user.getName(), user.getEmail(), "ROLE_USER")));
            }
        }

        // 2. Try Admin
        Optional<Admin> adminOpt = adminRepository.findById(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword()) || password.equals(admin.getPassword())) {
                if (!admin.getPassword().startsWith("$2a$")) {
                    admin.setPassword(passwordEncoder.encode(password));
                    adminRepository.save(admin);
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String token = jwtService.generateToken(userDetails, "ROLE_ADMIN");
                return ResponseEntity.ok(ApiResponse.ok("Admin login successful", new AuthResponse(token, admin.getAdminId(), admin.getName(), admin.getEmail(), "ROLE_ADMIN")));
            }
        }

        // 3. Try Driver
        Optional<Driver> driverOpt = driverRepository.findById(username);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            if (passwordEncoder.matches(password, driver.getPassword()) || password.equals(driver.getPassword())) {
                if (!driver.getPassword().startsWith("$2a$")) {
                    driver.setPassword(passwordEncoder.encode(password));
                    driverRepository.save(driver);
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String token = jwtService.generateToken(userDetails, "ROLE_DRIVER");
                return ResponseEntity.ok(ApiResponse.ok("Driver login successful", new AuthResponse(token, driver.getDriverId(), driver.getName(), driver.getEmail(), "ROLE_DRIVER")));
            }
        }

        return ResponseEntity.status(401).body(ApiResponse.error("Invalid credentials. Please verify your ID and password."));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        String role = (request.getRole() != null) ? request.getRole().toUpperCase() : "USER";
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        if ("ADMIN".equals(role)) {
            if (adminRepository.existsById(request.getUserId())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Admin ID already exists"));
            }
            Admin admin = new Admin(request.getUserId(), request.getName(), request.getEmail(), encodedPassword);
            adminRepository.save(admin);
            return ResponseEntity.ok(ApiResponse.ok("Admin registered successfully", admin.getAdminId()));
        } else if ("DRIVER".equals(role)) {
            if (driverRepository.existsById(request.getUserId())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Driver ID already exists"));
            }
            Driver driver = new Driver(request.getUserId(), request.getName(), request.getLicenseNumber(), request.getPhone(), request.getEmail(), encodedPassword);
            driverRepository.save(driver);
            return ResponseEntity.ok(ApiResponse.ok("Driver registered successfully", driver.getDriverId()));
        } else {
            if (userRepository.existsById(request.getUserId())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("User ID already exists"));
            }
            User user = new User(request.getUserId(), request.getName(), request.getEmail(), request.getPhone(), encodedPassword);
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.ok("Customer registered successfully", user.getUserId()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }

        String username = auth.getName();
        Optional<User> u = userRepository.findById(username);
        if (u.isPresent()) return ResponseEntity.ok(ApiResponse.ok("User Profile", u.get()));

        Optional<Admin> a = adminRepository.findById(username);
        if (a.isPresent()) return ResponseEntity.ok(ApiResponse.ok("Admin Profile", a.get()));

        Optional<Driver> d = driverRepository.findById(username);
        if (d.isPresent()) return ResponseEntity.ok(ApiResponse.ok("Driver Profile", d.get()));

        return ResponseEntity.status(404).body(ApiResponse.error("User profile not found"));
    }
}
