package com.yearup.dealership.db;

import java.math.*;
import java.time.*;

public final class SalesDao {
    private SalesDao() throws InstantiationException {
        throw new InstantiationException("No instance for you");
    }

    public static void addSalesContract(String vin, LocalDate date, double price) {
        CarDealership.SalesContracts.builder()
            .withVin(vin)
            .withSaleDate(date)
            .withPrice(BigDecimal.valueOf(price));
        CarDealership.commit();
    }
}
