package com.OOP.rentalX;

import com.OOP.rentalX.model.Vehicle;
import com.OOP.rentalX.util.SelectionSortUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SelectionSortUtilTest {

    @Test
    @DisplayName("SelectionSortUtil correctly sorts vehicles in ascending order of rent price")
    void testSortByRentPrice() {
        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(new Vehicle("V1", "Toyota Camry", "Sedan", true, 80.0, "", "D1"));
        vehicles.add(new Vehicle("V2", "Ferrari 488", "Supercar", true, 350.0, "", "D2"));
        vehicles.add(new Vehicle("V3", "Honda Civic", "Sedan", true, 55.0, "", "D3"));
        vehicles.add(new Vehicle("V4", "BMW M5", "Luxury", true, 180.0, "", "D4"));

        SelectionSortUtil.sortByRentPrice(vehicles);

        assertEquals("V3", vehicles.get(0).getVehicleId());
        assertEquals(55.0, vehicles.get(0).getRentPrice());
        assertEquals("V1", vehicles.get(1).getVehicleId());
        assertEquals(80.0, vehicles.get(1).getRentPrice());
        assertEquals("V4", vehicles.get(2).getVehicleId());
        assertEquals(180.0, vehicles.get(2).getRentPrice());
        assertEquals("V2", vehicles.get(3).getVehicleId());
        assertEquals(350.0, vehicles.get(3).getRentPrice());
    }

    @Test
    @DisplayName("SelectionSortUtil handles empty and single-element lists gracefully")
    void testSortEmptyAndSingle() {
        List<Vehicle> emptyList = new ArrayList<>();
        SelectionSortUtil.sortByRentPrice(emptyList);
        assertTrue(emptyList.isEmpty());

        List<Vehicle> single = new ArrayList<>();
        single.add(new Vehicle("V1", "Tesla", "Electric", true, 90.0, "", "D1"));
        SelectionSortUtil.sortByRentPrice(single);
        assertEquals(1, single.size());
    }
}
