package com.yearup.dealership.db;

public final class InventoryDao {
    private InventoryDao() throws InstantiationException {
        throw new InstantiationException("No instance for you");
    }

    public static void addVehicleToInventory(String vin, int dealershipId) {
        CarDealership.Inventory.builder()
            .withVin(vin)
            .withDealershipId(dealershipId)
            .build();
        CarDealership.commit();
    }

    public static void removeVehicleFromInventory(String vin) {
        CarDealership.Inventory.fetchByVin(vin).forEach(CarDealership.Inventory::delete);
        CarDealership.commit();
    }
}
