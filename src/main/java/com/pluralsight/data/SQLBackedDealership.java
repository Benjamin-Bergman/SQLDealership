/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight.data;

import com.pluralsight.data.contracts.*;
import com.pluralsight.data.contracts.Contract;
import com.pluralsight.sql.*;
import manifold.ext.rt.api.*;
import org.jetbrains.annotations.*;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Represents a {@link Dealership} held in an SQL database.
 */
@SuppressWarnings({"StaticMethodReferencedViaSubclass", "FeatureEnvy"})
public class SQLBackedDealership implements Dealership {
    private final int id;
    private final CarDealership.Dealerships entity;
    private final SQLBackedContractManager contracts;

    /**
     * @param id The id of the database to use
     */
    public SQLBackedDealership(int id) {
        this.id = id;
        entity = "[.sql/] SELECT * FROM dealerships WHERE dealership_id = :id".fetchOne(id);
        contracts = new SQLBackedContractManager();
    }

    @Override
    public String getPhone() {
        return "[.sql/] SELECT phone FROM dealerships WHERE dealership_id = :id".fetchOne(id).phone;
    }

    @Override
    public String getAddress() {
        return "[.sql/] SELECT address FROM dealerships WHERE dealership_id = :id".fetchOne(id).address;
    }

    @Override
    public String getDisplayName() {
        return "[.sql/] SELECT name FROM dealerships WHERE dealership_id = :id".fetchOne(id).name;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        var all = """
            [.sql/] SELECT v.*
            FROM vehicles v NATURAL JOIN inventory NATURAL JOIN dealerships d
            WHERE d.dealership_id = :id
            """.fetch(id);

        return StreamSupport.stream(all.spliterator(), false)
            .map(en -> new Vehicle(
                en.vin, en.year, en.make, en.model,
                en.vehicletype, en.color, en.odometerreading, en.price.doubleValue()))
            .toList();
    }

    private void addOne(Vehicle vehicle) {
        var vehicleEntity = CarDealership.Vehicles.create(
            vehicle.vin(), vehicle.year(), vehicle.make(), vehicle.model(),
            vehicle.vehicleType(), vehicle.color(), vehicle.odometer(), BigDecimal.valueOf(vehicle.price())
        );
        CarDealership.Inventory.create(entity, vehicleEntity);
    }

    @Override
    public void add(Vehicle vehicle) {
        addOne(vehicle);
        CarDealership.commit();
    }

    @Override
    public void addAll(Collection<Vehicle> vehicles) {
        vehicles.forEach(this::addOne);
        CarDealership.commit();
    }

    @Override
    public boolean remove(Vehicle vehicle) {
        //noinspection ReassignedVariable
        Stream<? extends Deletable> matching =
            StreamSupport.stream(
                "[.sql/] SELECT * FROM vehicles WHERE vin = :vin".fetch(vehicle.vin()).spliterator(),
                false);

        matching = Stream.concat(matching,
            StreamSupport.stream(
                "[.sql/] SELECT * FROM inventory WHERE vin = :vin".fetch(vehicle.vin()).spliterator(),
                false)
        );

        matching = Stream.concat(matching,
            StreamSupport.stream(
                "[.sql/] SELECT * FROM lease_contracts WHERE vin = :vin".fetch(vehicle.vin()).spliterator(),
                false)
        );

        matching = Stream.concat(matching,
            StreamSupport.stream(
                "[.sql/] SELECT * FROM sales_contracts WHERE vin = :vin".fetch(vehicle.vin()).spliterator(),
                false)
        );

        boolean[] anyDone = {false};
        matching.forEach(deletable -> {
            anyDone[0] = true;
            deletable.delete();
        });

        if (!anyDone[0])
            return false;

        CarDealership.commit();
        return true;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Clearing the database is a very unsafe operation, so it is not allowed.");
    }

    /**
     * @return The contract manager for this database
     */
    public SimpleList<Contract> getContractManager() {
        return contracts;
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private static class SQLBackedContractManager implements SimpleList<Contract> {
        @Override
        public void add(Contract item) {
            if (item instanceof SalesContract sale)
                CarDealership.SalesContracts.create(
                    sale.vehicleSold.vin(),
                    BigDecimal.valueOf(sale.salesTax),
                    BigDecimal.valueOf(sale.recordingFee),
                    BigDecimal.valueOf(sale.processingFee),
                    sale.financed
                );
            else if (item instanceof LeaseContract lease)
                CarDealership.LeaseContracts.create(
                    lease.vehicleSold.vin(),
                    BigDecimal.valueOf(lease.expectedEndingValue),
                    BigDecimal.valueOf(lease.leaseFee),
                    lease.paymentLength
                );

            CarDealership.commit();
        }

        @Override
        public boolean remove(Contract item) {
            Iterable<? extends Deletable> items;
            if (item instanceof SalesContract sale)
                items = "[.sql/] SELECT * FROM sales_contracts WHERE vin = :vin".fetch(sale.vehicleSold.vin());
            else if (item instanceof LeaseContract lease)
                items = "[.sql/] SELECT * FROM lease_contracts WHERE vin = :vin".fetch(lease.vehicleSold.vin());
            else
                throw new IllegalArgumentException("Unexpected argument! Got type ${item.getClass().getSimpleName()}");

            var matching = items.iterator();
            if (!matching.hasNext())
                return false;
            do matching.next().delete();
            while (matching.hasNext());
            CarDealership.commit();
            return true;
        }

        private static List<Contract> fetchAll() {
            var sales = "[.sql/] SELECT s.*, v.* FROM sales_contracts s NATURAL JOIN vehicles v".fetch();
            var leases = "[.sql/] SELECT l.*, v.* FROM lease_contracts l NATURAL JOIN vehicles v".fetch();

            return Stream.concat(
                StreamSupport.stream(sales.spliterator(), false)
                    .map(en -> (Contract) new SalesContract(
                        new Vehicle(en.vehicles.vin, en.vehicles.year, en.vehicles.make, en.vehicles.model,
                            en.vehicles.vehicletype, en.vehicles.color, en.vehicles.odometerreading, en.vehicles.price.doubleValue()),
                        "",
                        "",
                        en.timestamp.toLocalDate(),
                        en.salesTax.doubleValue(),
                        en.recordingFee.doubleValue(),
                        en.processingFee.doubleValue(),
                        en.financed
                    )),
                StreamSupport.stream(leases.spliterator(), false)
                    .map(en -> (Contract) new LeaseContract(
                        new Vehicle(en.vehicles.vin, en.vehicles.year, en.vehicles.make, en.vehicles.model,
                            en.vehicles.vehicletype, en.vehicles.color, en.vehicles.odometerreading, en.vehicles.price.doubleValue()),
                        "",
                        "",
                        en.timestamp.toLocalDate(),
                        en.expectedEndingValue.doubleValue(),
                        en.leaseFee.doubleValue()
                    ))
            ).toList();
        }

        @NotNull
        @Override
        public Iterator<Contract> iterator() {
            return fetchAll().iterator();
        }

        @Override
        public void forEach(Consumer<? super Contract> action) {
            fetchAll().forEach(action);
        }

        @Override
        public Spliterator<Contract> spliterator() {
            return fetchAll().spliterator();
        }
    }

    @FunctionalInterface
    @Structural
    private interface Deletable {
        void delete();
    }
}
