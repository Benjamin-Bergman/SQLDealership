/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.data.*;

@SuppressWarnings("UtilityClass")
final class Program {
    public static void main(String[] args) {
        var dealership = new SQLBackedDealership(1);
        var contracts = dealership.getContractManager();

        try (var ui = new DealershipUI(dealership, contracts, System.out, System.in)) {
            ui.display();
        }
    }
}
