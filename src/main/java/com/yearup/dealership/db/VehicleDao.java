package com.yearup.dealership.db;

import java.util.*;

public final class VehicleDao {
    private VehicleDao() {
        throw new InstantiationException("No instance for you");
    }

    public static void addVehicle(String vin, String make, String model, int year, boolean sold, String color, String type, int mileage, double price) {
        CarDealership.Vehicles.builder(vin)
            .withMake(make)
            .withModel(model)
            .withYear(year)
            .withSold(sold)
            .withColor(color)
            .withVehicletype(type)
            .withOdometer(mileage)
            .withPrice(price)
            .build();
        CarDealership.commit();
    }

    public static void removeVehicle(String VIN) {
        CarDealership.Vehicles.fetch(VIN).delete();
        CarDealership.commit();
    }

    public static List<CarDealership.Vehicles> searchByPriceRange(double minPrice, double maxPrice) {
        var list = new ArrayList<CarDealership.Vehicles>();
        "[.sql/] SELECT * FROM vehicles WHERE price >= :min AND price <= :max".fetch(minPrice, maxPrice).forEach(list::add);
        return list;
    }

    public static List<CarDealership.Vehicles> searchByMakeModel(String make, String model) {
        var list = new ArrayList<CarDealership.Vehicles>();
        "[.sql/] SELECT * FROM vehicles WHERE make LIKE :make AND model LIKE :model".fetch(make, model).forEach(list::add);
        return list;
    }

    public static List<CarDealership.Vehicles> searchByYearRange(int minYear, int maxYear) {
        var list = new ArrayList<CarDealership.Vehicles>();
        "[.sql/] SELECT * FROM vehicles WHERE year BETWEEN :min AND  :max".fetch(minYear, maxYear).forEach(list::add);
        return list;
    }

    public static List<CarDealership.Vehicles> searchByColor(String color) {
        var list = new ArrayList<CarDealership.Vehicles>();
        "[.sql/] SELECT * FROM vehicles WHERE color LIKE :color".fetch(color).forEach(list::add);
        return list;
    }

    public static List<CarDealership.Vehicles> searchByMileageRange(int minMileage, int maxMileage) {
        var list = new ArrayList<CarDealership.Vehicles>();
        "[.sql/] SELECT * FROM vehicles WHERE odometer >= :min AND odometer <= :max".fetch(minMileage, maxMileage).forEach(list::add);
        return list;
    }

    public static List<CarDealership.Vehicles> searchByType(String type) {
        var list = new ArrayList<CarDealership.Vehicles>();
        "[.sql/] SELECT * FROM vehicles WHERE vehicleType LIKE :type".fetch(type).forEach(list::add);
        return list;
    }
}
