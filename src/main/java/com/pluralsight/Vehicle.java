/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import java.util.*;

/**
 * Represents a vehicle.
 *
 * @param vin         The vehicle's identification number
 * @param year        The vehicle's model year
 * @param make        The vehicle's make
 * @param model       The vehicle's model
 * @param vehicleType The vehicle's type (e.g. {@code "SUV"} or {@code "Truck"})
 * @param color       The vehicle's color
 * @param odometer    The vehicle's odometer reading
 * @param price       The vehicle's price
 */
public record Vehicle(int vin, int year,
                      String make, String model,
                      String vehicleType, String color,
                      int odometer, double price) {
    public static Vehicle fromCSV(String s) {
        assert s != null : "s comes from BufferedReader.lines()";
        var parts = s.split("\\|");
        if (parts.length != 8)
            return null;
        var vin = parseInt(parts[0]);
        if (vin.isEmpty())
            return null;
        var year = parseInt(parts[1]);
        if (year.isEmpty())
            return null;
        var odometer = parseInt(parts[6]);
        if (odometer.isEmpty())
            return null;
        var price = parseDouble(parts[7]);
        if (price.isEmpty())
            return null;

        return new Vehicle(vin.getAsInt(), year.getAsInt(),
            parts[2], parts[3],
            parts[4], parts[5],
            odometer.getAsInt(), price.getAsDouble());
    }

    private static OptionalInt parseInt(String s) {
        try {
            return OptionalInt.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    private static OptionalDouble parseDouble(String s) {
        try {
            return OptionalDouble.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    public String toCSV() {
        return "%d|%d|%s|%s|%s|%s|%d|%.2f".formatted(
            vin(), year(),
            make(), model(),
            vehicleType(), color(),
            odometer(), price());
    }

    @Override
    public String toString() {
        return "$%.2f - %d - %s %d %s %s (%s), %dmi"
            .formatted(price, vin, color, year, make, model, vehicleType, odometer);
    }
}
