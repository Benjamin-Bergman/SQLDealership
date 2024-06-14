/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import com.pluralsight.data.*;
import com.pluralsight.data.contracts.*;
import com.pluralsight.sql.*;

import java.io.*;

@SuppressWarnings("UtilityClass")
final class Program {
//    private static final File FILE_PATH = new File("inventory.csv");
//    private static final File CONTRACT_PATH = new File("contracts.csv");

    public static void main(String[] args) {
//        var dealership = new ResourceBackedDealership(
//            new BasicDealership("Default_Name", "Default_Address", "Default_Phone"),
//            () -> new FileReader(FILE_PATH),
//            append -> new FileWriter(FILE_PATH, append));
//        var contracts = new FileBackedContractList(CONTRACT_PATH);

        var dealership = new SQLBackedDealership(1);
        var contracts = dealership.getContractManager();

        try (var ui = new DealershipUI(dealership, contracts, System.out, System.in)) {
            ui.display();
        }
    }
}
