package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Admin;
import com.OOP.rentalX.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(Admin admin) {
        if (!admin.getPassword().startsWith("$2a$")) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        adminRepository.save(admin);
    }

    public Admin login(String id, String password) {
        Optional<Admin> adminOpt = adminRepository.findById(id);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(password, admin.getPassword()) || password.equals(admin.getPassword())) {
                return admin;
            }
        }
        return null;
    }

    @Transactional
    public boolean deleteAdmin(String adminId) {
        if (adminRepository.existsById(adminId)) {
            adminRepository.deleteById(adminId);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updateAdmin(Admin updatedAdmin) {
        if (adminRepository.existsById(updatedAdmin.getAdminId())) {
            Admin existing = adminRepository.findById(updatedAdmin.getAdminId()).get();
            existing.setName(updatedAdmin.getName());
            existing.setEmail(updatedAdmin.getEmail());
            if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().trim().isEmpty()) {
                if (!updatedAdmin.getPassword().equals(existing.getPassword())) {
                    existing.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
                }
            }
            adminRepository.save(existing);
            return true;
        }
        return false;
    }

    public List<Admin> getAll() {
        return adminRepository.findAll();
    }
}
