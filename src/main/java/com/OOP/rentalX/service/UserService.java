package com.OOP.rentalX.service;

import com.OOP.rentalX.model.User;
import com.OOP.rentalX.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    public User login(String userId, String password) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword()) || password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public User getProfile(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public void updateProfile(User updatedUser) {
        userRepository.findById(updatedUser.getUserId()).ifPresent(existing -> {
            existing.setName(updatedUser.getName());
            existing.setEmail(updatedUser.getEmail());
            existing.setPhone(updatedUser.getPhone());
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                if (!updatedUser.getPassword().equals(existing.getPassword())) {
                    existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }
            }
            userRepository.save(existing);
        });
    }

    @Transactional
    public void deleteProfile(String userId) {
        userRepository.deleteById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
