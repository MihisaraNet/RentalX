package com.OOP.rentalX;

import com.OOP.rentalX.model.Vehicle;
import com.OOP.rentalX.util.RentedVehicleList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RentedVehicleListTest {

    private RentedVehicleList list;

    @BeforeEach
    void setUp() {
        list = new RentedVehicleList();
    }

    @Test
    @DisplayName("RentedVehicleList allows adding, querying, and deleting linked list nodes")
    void testLinkedListOperations() {
        Vehicle v1 = new Vehicle("V001", "Tesla Model 3", "Electric", false, 85.0, "", "D1");
        Vehicle v2 = new Vehicle("V002", "BMW 520d", "Luxury", false, 110.0, "", "D2");
        Vehicle v3 = new Vehicle("V003", "Audi A6", "Sedan", false, 105.0, "", "D3");

        list.add(v1);
        list.add(v2);
        list.add(v3);

        assertTrue(list.contains("V001"));
        assertTrue(list.contains("V002"));
        assertTrue(list.contains("V003"));
        assertFalse(list.contains("V999"));

        List<Vehicle> all = list.toList();
        assertEquals(3, all.size());
        assertEquals("V001", all.get(0).getVehicleId());

        // Test delete middle
        boolean deletedMiddle = list.delete("V002");
        assertTrue(deletedMiddle);
        assertFalse(list.contains("V002"));
        assertEquals(2, list.toList().size());

        // Test delete head
        boolean deletedHead = list.delete("V001");
        assertTrue(deletedHead);
        assertFalse(list.contains("V001"));
        assertEquals(1, list.toList().size());
    }
}
