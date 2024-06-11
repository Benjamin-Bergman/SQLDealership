/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import java.util.*;

final class TestDealershipImpl implements Dealership {
    int countAdd, countAddAll, countRemove, countClear, countGetAll;
    boolean removeSuccess, emptyData;
    List<Vehicle> getAllResult = List.of();
    Collection<Vehicle> addAllArgument;
    Vehicle addArgument;

    @Override
    public String getPhone() {
        return emptyData ? "" : "TEST_PHONE";
    }

    @Override
    public String getAddress() {
        return emptyData ? "" : "TEST_ADDRESS";
    }

    @Override
    public String getDisplayName() {
        return emptyData ? "" : "TEST_DISPLAY_NAME";
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        countGetAll++;
        return getAllResult;
    }

    @Override
    public void add(Vehicle vehicle) {
        countAdd++;
        addArgument = vehicle;
    }

    @Override
    public void addAll(Collection<Vehicle> vehicles) {
        countAddAll++;
        addAllArgument = vehicles;
    }

    @Override
    public boolean remove(Vehicle vehicle) {
        countRemove++;
        return removeSuccess;
    }

    @Override
    public void clear() {
        countClear++;
    }
}
