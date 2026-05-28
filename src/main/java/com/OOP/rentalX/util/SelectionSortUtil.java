package com.OOP.rentalX.util;

import com.OOP.rentalX.model.Vehicle;

import java.util.List;

public class SelectionSortUtil {

    public static void sortByRentPrice(List<Vehicle> vehicles) {
        int n = vehicles.size();
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (vehicles.get(j).getRentPrice() < vehicles.get(minIdx).getRentPrice()) {
                    minIdx = j;
                }
            }
            // Swap
            Vehicle temp = vehicles.get(minIdx);
            vehicles.set(minIdx, vehicles.get(i));
            vehicles.set(i, temp);
        }
    }
}
