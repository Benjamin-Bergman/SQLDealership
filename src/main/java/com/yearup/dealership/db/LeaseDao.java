package com.yearup.dealership.db;

import java.math.*;
import java.time.*;

public final class LeaseDao {
    private LeaseDao() throws InstantiationException {
        throw new InstantiationException("No instance for you");
    }

    public static void addLeaseContract(String vin, LocalDate start, LocalDate end, double payment) {
        CarDealership.LeaseContracts.builder()
            .withVin(vin)
            .withLeaseStart(start)
            .withLeaseEnd(end)
            .withMonthlyPayment(BigDecimal.valueOf(payment))
            .build();
        CarDealership.commit();
    }
}
