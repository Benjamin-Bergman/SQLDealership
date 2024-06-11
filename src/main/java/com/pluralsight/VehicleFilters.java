/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import java.util.function.*;
import java.util.stream.*;

/**
 * A collection of common {@link Predicate}s for filtering {@link Vehicle}s.
 */
@SuppressWarnings("UtilityClass")
public final class VehicleFilters {
    private VehicleFilters() {
        throw new InstantiationException("This class cannot be instantiated.");
    }

    /**
     * @param min The minimum price for a vehicle
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> minPrice(double min) {
        return v -> v.price() >= min;
    }

    /**
     * @param max The maximum price for a vehicle
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> maxPrice(double max) {
        return v -> v.price() <= max;
    }

    /**
     * @param make The make of a vehicle, fuzzy searched
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> make(String make) {
        var cleaned = make.trim().toLowerCase();
        return v -> v.make().toLowerCase().contains(cleaned);
    }

    /**
     * @param model The model of a vehicle, fuzzy searched
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> model(String model) {
        var cleaned = model.trim().toLowerCase();
        return v -> v.model().toLowerCase().contains(cleaned);
    }

    /**
     * @param min The minimum year of a vehicle
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> minYear(int min) {
        return v -> v.year() >= min;
    }

    /**
     * @param max The maximum year of a vehicle
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> maxYear(int max) {
        return v -> v.year() <= max;
    }

    /**
     * @param color The color of a vehicle, fuzzy searched
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> color(String color) {
        var cleaned = color.trim().toLowerCase();
        return v -> v.color().toLowerCase().contains(cleaned);
    }

    /**
     * @param min The minimum odometer reading of a vehicle
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> minOdometer(int min) {
        return v -> v.odometer() >= min;
    }

    /**
     * @param max The maximum odometer reading of a vehicle
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> maxOdometer(int max) {
        return v -> v.odometer() <= max;
    }

    /**
     * @param type The type of vehicle, fuzzy searched
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> type(String type) {
        var cleaned = type.trim().toLowerCase();
        return v -> v.vehicleType().toLowerCase().contains(cleaned);
    }

    /**
     * @param vin The exact VIN of a vehicle
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> vin(int vin) {
        return v -> v.vin() == vin;
    }

    /**
     * Allows every vehicle.
     *
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> all() {
        return v -> true;
    }

    /**
     * Allows vehicles that have not been sold or leased.
     *
     * @param list The contracts to check
     * @return A Predicate applying the condition
     */
    public static Predicate<Vehicle> available(Iterable<? extends Contract> list) {
        return v -> StreamSupport.stream(list.spliterator(), true).noneMatch(sale -> sale.vehicleSold.vin() == v.vin());
    }
}
