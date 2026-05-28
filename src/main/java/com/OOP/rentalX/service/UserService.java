package com.OOP.rentalX.service;

import com.OOP.rentalX.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final String FILE_PATH = "src/main/resources/users.txt";

    public void register(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toLine(user));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User login(String userId, String password) {
        return getAllUsers().stream()
                .filter(u -> u.getUserId().equals(userId) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public User getProfile(String userId) {
        return getAllUsers().stream()
                .filter(u -> u.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public void updateProfile(User updatedUser) {
        List<User> users = getAllUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                if (u.getUserId().equals(updatedUser.getUserId())) {
                    writer.write(toLine(updatedUser));
                } else {
                    writer.write(toLine(u));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteProfile(String userId) {
        List<User> users = getAllUsers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                if (!u.getUserId().equals(userId)) {
                    writer.write(toLine(u));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toLine(User u) {
        return u.getUserId() + "," + u.getName() + "," + u.getEmail() + "," + u.getPhone() + "," + u.getPassword();
    }

    private List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split(",", 5);
                list.add(new User(p[0], p[1], p[2], p[3], p[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
