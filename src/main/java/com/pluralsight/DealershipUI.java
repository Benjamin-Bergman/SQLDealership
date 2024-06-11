/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * Represents a user interface for interacting with a {@link Dealership} and a {@link SimpleList} of {@link Contract}s.
 */
@SuppressWarnings("FeatureEnvy")
public final class DealershipUI implements Closeable {
    @SuppressWarnings("StaticCollection")
    private static final List<String> DISPLAY_OPTIONS = List.of("0", "1", "2", "3", "4", "5", "6", "7");
    private static final Pattern MONEY_PATTERN = Pattern.compile("^\\$?(\\d*(?:\\.\\d\\d?)?)$");
    private static final Predicate<String> INT_PATTERN = Pattern.compile("^\\d+$").asPredicate();
    private final Dealership dealership;
    private final SimpleList<Contract> contracts;
    private final Scanner scanner;
    private final PrintStream out;

    /**
     * Creates a new instance of the UI.
     *
     * @param dealership The dealership this UI controls
     * @param contracts  The collection of contracts to use.
     * @param out        The output stream to write to
     * @param in         The input stream to read from
     */
    public DealershipUI(Dealership dealership, SimpleList<Contract> contracts, PrintStream out, InputStream in) {
        this.dealership = dealership;
        this.contracts = contracts;
        this.out = out;
        scanner = new Scanner(in);
    }

    /**
     * Runs the UI.
     */
    public void display() {
        out.println("Welcome to " + dealership.getDisplayName() + '!');
        loop:
        while (true) {
            out.print("""
                --SEARCH--
                0 - By everything
                1 - By price
                2 - By make/model
                3 - By year
                4 - By color
                5 - By odometer
                6 - By type
                7 - Show all
                --OTHER--
                8 - Add vehicle
                9 - Remove vehicle
                10 - Buy vehicle
                99 - Exit
                Choose an option:\s""");
            var input = scanner.nextLine().trim();
            if (DISPLAY_OPTIONS.contains(input)) {
                displayVehicles(queryFilterParams(input));
                readKey();
                continue;
            }

            switch (input) {
                case "8" -> addVehicle();
                case "9" -> removeVehicle();
                case "10" -> processSale();
                case "99" -> {
                    break loop;
                }
                default -> out.println("Unknown option \"$input\"! Please try again.");
            }
        }

        out.println("Thanks for stopping by!");
    }

    @Override
    public void close() {
        scanner.close();
    }

    @SuppressWarnings("ReassignedVariable")
    private void processSale() {
        //noinspection HardcodedFileSeparator
        System.out.print("Is this a sale? [y/n] ");
        var sale = queryYN();
        Vehicle vehicle;
        do {
            var vin = queryIntValue("VIN", null);
            //noinspection ObjectAllocationInLoop
            vehicle = dealership.allVehicles.stream().filter(v -> v.vin() == vin).findFirst()
                .orElseGet(() -> {
                    System.out.println("Couldn't find that vehicle. Try again.");
                    return null;
                });
            //noinspection ObjectAllocationInLoop
            if (!sale && vehicle != null && vehicle.year() + 3 < LocalDate.now().year) {
                //noinspection AssignmentToNull
                vehicle = null;
                System.out.println("That vehicle is too old to lease. Try again.");
            }
        }
        while (vehicle == null);
        var name = queryStringValue("customer's name", false);
        var email = queryStringValue("customer's email address", false);
        if (sale) {
            //noinspection HardcodedFileSeparator
            System.out.print("Is this financed? [y/n] ");
            var financed = queryYN();
            var contract = new SalesContract(vehicle, email, name, 0.05, 100, vehicle.price() < 10_000 ? 295 : 495, financed);
            contracts.add(contract);
            System.out.println("Sold vehicle #${vehicle.vin()} to $name at $email " + (financed ? "with financing." : "without financing."));
        } else {
            var contract = new LeaseContract(vehicle, email, name, vehicle.price() / 2, vehicle.price() * 0.07);
            contracts.add(contract);
            System.out.println("Leased vehicle #${vehicle.vin()} to $name at $email.");
        }

        readKey();
    }

    private void removeVehicle() {
        var filter = queryArbitraryFilter() & VehicleFilters.available(contracts);

        var found = dealership
            .getAllVehicles()
            .stream()
            .filter(filter)
            .toList();

        if (found.isEmpty()) {
            out.println("Found no matching vehicles. Aborting...");
            readKey();
            return;
        }

        //noinspection HardcodedFileSeparator
        out.print(
            found.size() == 1 ?
                """
                    Found one matching vehicle:
                    ${found[0]}
                    Remove it? [y/n]\s"""
                : "Found ${found.size()} matching vehicles. Remove all of them? [y/n] "
        );

        if (queryYN()) {
            for (var v : found)
                dealership.remove(v);
            out.println("Removed ${found.size()} vehicles.");
        } else
            out.println("Nothing removed.");

        readKey();
    }

    private boolean queryYN() {
        while (true) {
            var input = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(input) || "n".equals(input)
                || "yes".equals(input) || "no".equals(input))
                return "y".equals(input) || "yes".equals(input);
            //noinspection HardcodedFileSeparator
            out.print("Unknown option \"$input\". Try again: [y/n] ");
        }
    }

    private Predicate<Vehicle> queryArbitraryFilter() {
        var price = queryMoneyValue("vehicle's", -1.0);
        var make = queryStringValue("make", true);
        var model = queryStringValue("model", true);
        var year = queryIntValue("year", -1);
        var color = queryStringValue("color", true);
        var odometer = queryIntValue("odometer reading", -1);
        var type = queryStringValue("type", true);
        var vin = queryIntValue("VIN", -1);

        //noinspection FloatingPointEquality
        return Stream.of(
                price == -1 ? null : VehicleFilters.minPrice(price) & VehicleFilters.maxPrice(price),
                make.isEmpty() ? null : VehicleFilters.make(make),
                model.isEmpty() ? null : VehicleFilters.model(model),
                year == -1 ? null : VehicleFilters.minYear(year) & VehicleFilters.maxYear(year),
                color.isEmpty() ? null : VehicleFilters.color(color),
                odometer == -1 ? null : VehicleFilters.minOdometer(odometer) & VehicleFilters.maxOdometer(odometer),
                type.isEmpty() ? null : VehicleFilters.type(type),
                vin == -1 ? null : VehicleFilters.vin(vin)
            )
            .filter(Objects::nonNull)
            .reduce(VehicleFilters.all(), Predicate::and);
    }

    private void addVehicle() {
        var price = queryMoneyValue("vehicle's", null);
        var make = queryStringValue("make", false);
        var model = queryStringValue("model", false);
        var year = queryIntValue("year", null);
        var color = queryStringValue("color", false);
        var odometer = queryIntValue("odometer reading", null);
        var type = queryStringValue("type", false);
        var vin = queryIntValue("VIN", null);

        var v = new Vehicle(vin, year, make, model, type, color, odometer, price);
        dealership.add(v);
        out.print("""
            Successfully added the vehicle:
            $v
            """);
        readKey();
    }

    private void readKey() {
        out.println("Press enter to continue...");
        scanner.nextLine();
    }

    private Predicate<Vehicle> queryFilterParams(String input) {
        return switch (input) {
            case "0" -> queryArbitraryFilter();
            case "1" -> {
                var min = queryMoneyValue("minimum", Double.NEGATIVE_INFINITY);
                var max = queryMoneyValue("maximum", Double.POSITIVE_INFINITY);
                yield VehicleFilters.minPrice(min) & VehicleFilters.maxPrice(max);
            }
            case "2" -> {
                var make = queryStringValue("make", true);
                var model = queryStringValue("model", true);
                yield VehicleFilters.make(make) & VehicleFilters.model(model);
            }
            case "3" -> {
                var min = queryIntValue("minimum year", Integer.MIN_VALUE);
                var max = queryIntValue("maximum year", Integer.MAX_VALUE);
                yield VehicleFilters.minYear(min) & VehicleFilters.maxYear(max);
            }
            case "4" -> VehicleFilters.color(queryStringValue("color", true));
            case "5" -> {
                var min = queryIntValue("minimum reading", Integer.MIN_VALUE);
                var max = queryIntValue("maximum reading", Integer.MAX_VALUE);
                yield VehicleFilters.minOdometer(min) & VehicleFilters.maxOdometer(max);
            }
            case "6" -> VehicleFilters.type(queryStringValue("type", true));
            case "7" -> VehicleFilters.all();
            default -> //noinspection ProhibitedExceptionThrown
                throw new RuntimeException("Unreachable");
        } & VehicleFilters.available(contracts);
    }

    private int queryIntValue(String which, Integer defaultValue) {
        while (true) {
            out.print("Enter the $which: ");
            var input = scanner.nextLine().trim();
            if (defaultValue != null && input.isEmpty())
                return defaultValue;
            if (INT_PATTERN.test(input)) try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ignored) {
                // Reachable for an input like 9999999999999999999
            }
            out.println("Bad input, please try again.");
        }
    }

    private String queryStringValue(String which, boolean allowEmpty) {
        while (true) {
            out.print("Enter the $which: ");
            var input = scanner.nextLine().trim();
            if (allowEmpty || !input.isEmpty())
                return input;
            out.println("Bad input, please try again.");
        }
    }

    private double queryMoneyValue(String which, Double defaultValue) {
        while (true) {
            out.print("Enter the $which price: ");
            var input = scanner.nextLine().trim();
            if (defaultValue != null && input.isEmpty())
                return defaultValue;
            //noinspection ObjectAllocationInLoop
            var match = MONEY_PATTERN.matcher(input);
            if (match.matches()) try {
                return Double.parseDouble(match.group(1));
            } catch (NumberFormatException ignored) {
            }
            out.println("Bad input, please try again.");
        }
    }

    private void displayVehicles(Predicate<? super Vehicle> filter) {
        dealership
            .getAllVehicles()
            .stream()
            .filter(filter)
            .forEachOrdered(out::println);
    }
}
