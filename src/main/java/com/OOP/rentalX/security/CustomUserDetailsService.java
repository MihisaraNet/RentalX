package com.OOP.rentalX.security;

import com.OOP.rentalX.model.Admin;
import com.OOP.rentalX.model.Driver;
import com.OOP.rentalX.model.User;
import com.OOP.rentalX.repository.AdminRepository;
import com.OOP.rentalX.repository.DriverRepository;
import com.OOP.rentalX.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    AdminRepository adminRepository,
                                    DriverRepository driverRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Check User repository
        Optional<User> userOpt = userRepository.findById(username);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    u.getUserId(),
                    u.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        // 2. Check Admin repository
        Optional<Admin> adminOpt = adminRepository.findById(username);
        if (adminOpt.isPresent()) {
            Admin a = adminOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    a.getAdminId(),
                    a.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 3. Check Driver repository
        Optional<Driver> driverOpt = driverRepository.findById(username);
        if (driverOpt.isPresent()) {
            Driver d = driverOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    d.getDriverId(),
                    d.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_DRIVER"))
            );
        }

        throw new UsernameNotFoundException("User not found with ID / Username: " + username);
    }
}
