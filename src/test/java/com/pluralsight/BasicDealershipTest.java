/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class BasicDealershipTest {
    private static final String DISPLAY_NAME = "DISPLAY_NAME";
    private static final String ADDRESS = "ADDRESS";
    private static final String PHONE = "PHONE";
    private static final Vehicle DUMMY_VEHICLE = new Vehicle(0, 0, "", "", "", "", 0, 0);
    private static final Vehicle DUMMY_VEHICLE_2 = new Vehicle(10, 0, "", "", "", "", 0, 0);
    private Dealership dealership;

    @BeforeEach
    void createDealership() {
        dealership = new BasicDealership(DISPLAY_NAME, ADDRESS, PHONE);
    }

    @Test
    void test_getPhone() {
        assertEquals(PHONE, dealership.getPhone(), "getPhone() should return the provided phone number.");
    }

    @Test
    void test_getAddress() {
        assertEquals(ADDRESS, dealership.getAddress(), "getAddress() should return the provided address.");
    }

    @Test
    void test_getDisplayName() {
        assertEquals(DISPLAY_NAME, dealership.getDisplayName(), "getDisplayName() should return the provided display name.");
    }

    @Test
    void test_getAllVehicles() {
        var all = dealership.getAllVehicles();

        assertAll(
            "getAllVehicles()",
            () -> assertNotNull(all, "getAllVehicles() should return a value"),
            () -> assertTrue(all.isEmpty(), "getAllVehicles() should be empty initially"),
            () -> assertThrows(Exception.class,
                () -> all.add(DUMMY_VEHICLE),
                "getAllVehicles() should be immutable")
        );
    }

    @Test
    void test_remove() {
        assumeTrue(dealership.getAllVehicles().isEmpty(), "dealership should be empty");

        var result = dealership.remove(DUMMY_VEHICLE);
        assertAll(
            "remove()",
            () -> assertFalse(result, "Removing vehicle should fail"),
            () -> assertTrue(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should still be empty")
        );
    }

    @Test
    void test_clear() {
        assumeTrue(dealership.getAllVehicles().isEmpty(), "dealership should be empty");

        assertDoesNotThrow(dealership::clear, "Clearing should not fail");
        assertTrue(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should still be empty");
    }

    @Test
    void test_add() {
        assumeTrue(dealership.getAllVehicles().isEmpty(), "dealership should be empty");

        assertDoesNotThrow(() -> dealership.add(DUMMY_VEHICLE), "Adding a vehicle should succeed");

        var all = dealership.getAllVehicles();
        assertAll(
            "add()",
            () -> assertFalse(all.isEmpty(), "Vehicles should not be empty"),
            () -> assertEquals(1, all.size(), "There should be 1 vehicle"),
            () -> assertEquals(DUMMY_VEHICLE, all[0], "The only vehicle should be the one added")
        );
    }

    @Test
    void test_addAll_singleItem() {
        assumeTrue(dealership.getAllVehicles().isEmpty(), "dealership should be empty");

        assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE)), "Adding one vehicle should succeed");

        var all = dealership.getAllVehicles();
        assertAll(
            "add()",
            () -> assertFalse(all.isEmpty(), "Vehicles should not be empty"),
            () -> assertEquals(1, all.size(), "There should be 1 vehicle"),
            () -> assertEquals(DUMMY_VEHICLE, all[0], "The only vehicle should be the one added")
        );
    }

    @Test
    void test_addAll_multipleItems() {
        assumeTrue(dealership.getAllVehicles().isEmpty(), "dealership should be empty");

        var added = List.of(DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE);

        assertDoesNotThrow(() -> dealership.addAll(added), "Adding three vehicles should succeed");

        var all = dealership.getAllVehicles();
        assertAll(
            "add()",
            () -> assertFalse(all.isEmpty(), "Vehicles should not be empty"),
            () -> assertEquals(3, all.size(), "There should be 3 vehicles"),
            () -> assertArrayEquals(added.toArray(), all.toArray(), "Vehicles should match the ones added")
        );
    }

    @Nested
    class ContainingOne {
        @BeforeEach
        void addDummy() {
            dealership.add(DUMMY_VEHICLE);
        }

        @Test
        void test_add() {
            assumeTrue(dealership.getAllVehicles().size() == 1, "dealership should have 1 item");

            assertDoesNotThrow(() -> dealership.add(DUMMY_VEHICLE), "Adding a vehicle should succeed");

            var all = dealership.getAllVehicles();

            assertAll(
                "add()",
                () -> assertFalse(all.isEmpty(), "Vehicles should not be empty"),
                () -> assertEquals(2, all.size(), "There should be 2 vehicles")
            );
        }

        @Test
        void test_addAll() {
            assumeTrue(dealership.getAllVehicles().size() == 1, "dealership should have 1 item");

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE)), "Adding one vehicle should succeed");

            var all = dealership.getAllVehicles();

            assertAll(
                "add()",
                () -> assertFalse(all.isEmpty(), "Vehicles should not be empty"),
                () -> assertEquals(2, all.size(), "There should be 2 vehicles")
            );
        }

        @Test
        void test_remove() {
            assumeTrue(dealership.getAllVehicles().size() == 1, "dealership should have 1 item");

            var result = dealership.remove(DUMMY_VEHICLE);
            assertAll(
                "remove()",
                () -> assertTrue(result, "Removing vehicle should not fail"),
                () -> assertTrue(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should now be empty")
            );
        }

        @Test
        void test_remove_notPresent() {
            assumeTrue(dealership.getAllVehicles().size() == 1, "dealership should have 1 item");

            var result = dealership.remove(DUMMY_VEHICLE_2);
            assertAll(
                "remove()",
                () -> assertFalse(result, "Removing absent vehicle should fail"),
                () -> assertFalse(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should not be empty"),
                () -> assertEquals(1, dealership.getAllVehicles().size(), "getAllVehicles() should have 1 item")
            );
        }

        @Test
        void test_clear() {
            assumeTrue(dealership.getAllVehicles().size() == 1, "dealership should have 1 item");

            assertDoesNotThrow(dealership::clear, "Clearing should not fail");
            assertTrue(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should now be empty");
        }
    }

    @Nested
    class ContainingThree {
        @BeforeEach
        void addDummy() {
            dealership.addAll(List.of(DUMMY_VEHICLE, DUMMY_VEHICLE, DUMMY_VEHICLE));
        }

        @Test
        void test_add() {
            assumeTrue(dealership.getAllVehicles().size() == 3, "dealership should have 3 items");

            assertDoesNotThrow(() -> dealership.add(DUMMY_VEHICLE), "Adding a vehicle should succeed");

            var all = dealership.getAllVehicles();

            assertAll(
                "add()",
                () -> assertFalse(all.isEmpty(), "Vehicles should not be empty"),
                () -> assertEquals(4, all.size(), "There should be 4 vehicles")
            );
        }

        @Test
        void test_addAll() {
            assumeTrue(dealership.getAllVehicles().size() == 3, "dealership should have 3 items");

            assertDoesNotThrow(() -> dealership.addAll(List.of(DUMMY_VEHICLE)), "Adding one vehicle should succeed");

            var all = dealership.getAllVehicles();

            assertAll(
                "add()",
                () -> assertFalse(all.isEmpty(), "Vehicles should not be empty"),
                () -> assertEquals(4, all.size(), "There should be 2 vehicles")
            );
        }

        @Test
        void test_remove() {
            assumeTrue(dealership.getAllVehicles().size() == 3, "dealership should have 3 items");

            var result = dealership.remove(DUMMY_VEHICLE);
            assertAll(
                "remove()",
                () -> assertTrue(result, "Removing vehicle should not fail"),
                () -> assertFalse(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should not be empty"),
                () -> assertEquals(2, dealership.getAllVehicles().size(), "There should now be 2 vehicles")
            );
        }

        @Test
        void test_remove_notPresent() {
            assumeTrue(dealership.getAllVehicles().size() == 3, "dealership should have 3 items");

            var result = dealership.remove(DUMMY_VEHICLE_2);
            assertAll(
                "remove()",
                () -> assertFalse(result, "Removing absent vehicle should fail"),
                () -> assertFalse(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should not be empty"),
                () -> assertEquals(3, dealership.getAllVehicles().size(), "getAllVehicles() should still have 3 items")
            );
        }

        @Test
        void test_clear() {
            assumeTrue(dealership.getAllVehicles().size() == 3, "dealership should have 3 items");

            assertDoesNotThrow(dealership::clear, "Clearing should not fail");
            assertTrue(dealership.getAllVehicles().isEmpty(), "getAllVehicles() should now be empty");
        }
    }
}