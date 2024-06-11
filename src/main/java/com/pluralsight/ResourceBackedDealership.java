/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import java.io.*;
import java.util.*;
import java.util.function.*;

/**
 * Decorates a {@link Dealership} with file-saving semantics.
 */
public final class ResourceBackedDealership implements Dealership {
    private final Function<Boolean, Writer> writer;
    private final Dealership wrapped;
    private final String displayName, address, phone;

    /**
     * Creates a new ResourceBackedDealership.
     * Note that this replaces the contents of {@code wrapped} with the contents of the resource.
     *
     * @param wrapped The Dealership to decorate
     * @param reader  A supplier to create a readable stream of the resource.
     * @param writer  A supplier to create a writable stream of the resource.
     *                It accepts one boolean argument: if {@code true},
     *                the writer should implement appending semantics,
     *                otherwise it should implement overwriting semantics.
     */
    public ResourceBackedDealership(Dealership wrapped, Supplier<Reader> reader, Function<Boolean, Writer> writer) {
        this.wrapped = wrapped;
        this.writer = writer;

        boolean emptyFile, anyInvalid;

        try (var fr = reader.get();
             var br = new BufferedReader(fr)) {
            var header = br.readLine();

            String[] parts;
            //noinspection NestedAssignment
            if (header != null && (parts = header.split("\\|")).length == 3) {
                displayName = parts[0];
                address = parts[1];
                phone = parts[2];
                emptyFile = false;
            } else if (
                isValid(wrapped.getDisplayName())
                && isValid(wrapped.getAddress())
                && isValid(wrapped.getPhone())
            ) {
                displayName = wrapped.getDisplayName();
                address = wrapped.getAddress();
                phone = wrapped.getPhone();
                emptyFile = true;
            } else throw new IOException("Bad file header when reading");

            var items =
                br.lines()
                    .map(Vehicle::fromCSV)
                    .toList();

            anyInvalid = items.contains(null);

            if (!emptyFile)
                wrapped.clear();

            var filteredItems = items.stream().filter(Objects::nonNull).toList();

            if (!filteredItems.isEmpty())
                wrapped.addAll(filteredItems);
        }

        if (emptyFile || anyInvalid)
            writeAll();
    }

    private static boolean isValid(String str) {
        return str != null && !str.isEmpty();
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return wrapped.getAllVehicles();
    }

    @Override
    public void add(Vehicle vehicle) {
        wrapped.add(vehicle);
        try (var fw = writer.apply(true);
             var bw = new BufferedWriter(fw)) {
            bw.newLine();
            bw.write(vehicle.toCSV());
        }
    }

    @Override
    public void addAll(Collection<Vehicle> vehicles) {
        wrapped.addAll(vehicles);
        try (var fw = writer.apply(true);
             var bw = new BufferedWriter(fw)) {
            for (var v : vehicles) {
                bw.newLine();
                bw.write(v.toCSV());
            }
        }
    }

    @Override
    public boolean remove(Vehicle vehicle) {
        var success = wrapped.remove(vehicle);
        if (success) writeAll();
        return success;
    }

    @Override
    public void clear() {
        wrapped.clear();
        writeAll();
    }

    private void writeAll() {
        try (var fw = writer.apply(false);
             var bw = new BufferedWriter(fw)) {
            bw.write(displayName);
            bw.write('|');
            bw.write(address);
            bw.write('|');
            bw.write(phone);
            for (var v : getAllVehicles()) {
                bw.newLine();
                bw.write(v.toCSV());
            }
        }
    }
}
