/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {
    @Test
    void test_ToString() {
        Vehicle v = new Vehicle(12345, 2020, "Honda", "Civic", "Sedan", "Brown", 10000, 20_000.50);

        assertEquals("$20000.50 - 12345 - Brown 2020 Honda Civic (Sedan), 10000mi", v.toString(), "Should stringify correctly");
    }
}