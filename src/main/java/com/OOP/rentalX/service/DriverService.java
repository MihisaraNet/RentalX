package com.OOP.rentalX.service;

import com.OOP.rentalX.model.Driver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DriverService {

    private static final String FILE_PATH = "src/main/resources/drivers.txt";

    public void addDriver(Driver d) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(toLine(d));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Driver> getAllDrivers() {
        List<Driver> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(fromLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateDriver(Driver updated) {
        List<Driver> drivers = getAllDrivers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Driver d : drivers) {
                if (d.getDriverId().equals(updated.getDriverId())) {
                    writer.write(toLine(updated));
                } else {
                    writer.write(toLine(d));
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteDriver(String driverId) {
        List<Driver> drivers = getAllDrivers();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Driver d : drivers) {
                if (!d.getDriverId().equals(driverId)) {
                    writer.write(toLine(d));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toLine(Driver d) {
        return d.getDriverId() + "," + d.getName() + "," + d.getLicenseNumber() + "," +
                d.getPhone() + "," + d.getEmail() + "," + d.getPassword();
    }


    private Driver fromLine(String line) {
        String[] parts = line.split(",", 6);
        return new Driver(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]); // now 6 fields
    }

}

