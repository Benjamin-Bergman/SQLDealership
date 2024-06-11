/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import java.util.*;

/**
 * Represents a car dealership. Ultimately, this is just a collection of {@link Vehicle}s.
 */
public interface Dealership {
    /**
     * @return This dealership's phone number
     */
    String getPhone();

    /**
     * @return This dealership's address
     */
    String getAddress();

    /**
     * @return This dealership's name
     */
    String getDisplayName();

    /**
     * @return Every vehicle in this dealership
     */
    List<Vehicle> getAllVehicles();

    /**
     * Add a vehicle to this dealership's inventory.
     *
     * @param vehicle The vehicle to add
     */
    void add(Vehicle vehicle);

    /**
     * Adds a collection of vehicles to this dealership's inventory.
     *
     * @param vehicles The vehicles to add
     */
    void addAll(Collection<Vehicle> vehicles);

    /**
     * Removes a vehicle from this dealership's inventory
     *
     * @param vehicle The vehicle to remove
     * @return {@code true} if the operation was successful.
     */
    boolean remove(Vehicle vehicle);

    /**
     * Removes every vehicle from this dealership's inventory.
     */
    void clear();
}
