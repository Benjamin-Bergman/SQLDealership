/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import java.time.*;
import java.util.*;

/**
 * Static utilities for working with {@link Contract}s.
 */
@SuppressWarnings("UtilityClass")
public final class Contracts {
    private Contracts() {
        throw new InstantiationException("Utility class cannot be instantiated.");
    }

    /**
     * @param contract A contract
     * @return The contract represented as a row of CSV
     * @see #fromCSV
     */
    public static String makeCSV(Contract contract) {
        if (contract instanceof SalesContract sale)
            return "SALE|${sale.date}|${sale.customerName}|${sale.email}|${sale.vehicleSold.toCSV()}|${sale.salesTax}|${sale.recordingFee}|${sale.processingFee}|${sale.financed}";
        if (contract instanceof LeaseContract lease)
            return "LEASE|${lease.date}|${lease.customerName}|${lease.email}|${lease.vehicleSold.toCSV()}|${lease.expectedEndingValue}|${lease.leaseFee}";
        throw new IllegalArgumentException("Bad argument `contract` (type=${contract.getClass().getSimpleName()})");
    }

    /**
     * @param line A row of CSV data
     * @return The contract represented by the row of CSV
     * @see #makeCSV
     */
    public static Contract fromCSV(String line) {
        var parts = line.split("\\|");
        if (parts.isEmpty())
            throw new IllegalArgumentException("Empty string not allowed");
        return switch (parts[0]) {
            case "SALE" -> {
                if (parts.length != 16)
                    throw new IllegalArgumentException("Bad number of parts for a Sale (got ${parts.length})");
                var date = LocalDate.parse(parts[1]);
                var name = parts[2];
                var email = parts[3];
                var vehicle = Vehicle.fromCSV(String.join("|", Arrays.copyOfRange(parts, 4, 12)));
                var salesTax = Double.parseDouble(parts[12]);
                var recording = Double.parseDouble(parts[13]);
                var processing = Double.parseDouble(parts[14]);
                var financed = Boolean.getBoolean(parts[15]);
                yield new SalesContract(vehicle, email, name, date, salesTax, recording, processing, financed);
            }
            case "LEASE" -> {
                if (parts.length != 14)
                    throw new IllegalArgumentException("Bad number of parts for a Lease (got ${parts.length})");
                var date = LocalDate.parse(parts[1]);
                var name = parts[2];
                var email = parts[3];
                var vehicle = Vehicle.fromCSV(String.join("|", Arrays.copyOfRange(parts, 4, 12)));
                var ending = Double.parseDouble(parts[12]);
                var fee = Double.parseDouble(parts[13]);
                yield new LeaseContract(vehicle, email, name, date, ending, fee);
            }
            default -> throw new IllegalArgumentException("Bad first part \"${parts[0]}\"");
        };
    }
}
