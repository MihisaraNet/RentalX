package com.OOP.rentalX.util;

import com.OOP.rentalX.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class RentedVehicleList {
    private Node head;

    private static class Node {
        Vehicle data;
        Node next;

        Node(Vehicle data) {
            this.data = data;
        }
    }

    public void add(Vehicle vehicle) {
        Node newNode = new Node(vehicle);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) current = current.next;
            current.next = newNode;
        }
    }

    public void printAll() {
        Node current = head;
        while (current != null) {
            Vehicle v = current.data;
            System.out.println("Vehicle ID: " + v.getVehicleId() + ", Model: " + v.getModel());
            current = current.next;
        }
    }

    public boolean delete(String vehicleId) {
        if (head == null) return false;
        if (head.data.getVehicleId().equals(vehicleId)) {
            head = head.next;
            return true;
        }

        Node current = head;
        while (current.next != null && !current.next.data.getVehicleId().equals(vehicleId)) {
            current = current.next;
        }

        if (current.next == null) return false;

        current.next = current.next.next;
        return true;
    }

    public List<Vehicle> toList() {
        List<Vehicle> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }


    public boolean contains(String vehicleId) {
        Node current = head;
        while (current != null) {
            if (current.data.getVehicleId().equals(vehicleId)) return true;
            current = current.next;
        }
        return false;
    }
}
