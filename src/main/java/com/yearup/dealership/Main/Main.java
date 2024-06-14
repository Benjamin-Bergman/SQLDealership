package com.yearup.dealership.Main;

import com.yearup.dealership.db.*;

import java.time.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Create a Scanner object for user input
        Scanner scanner = new Scanner(System.in);

        boolean exit = false;
        while (!exit) {
            System.out.println("Main Menu:");
            System.out.println("1. Search vehicles");
            System.out.println("2. Add a vehicle");
            System.out.println("3. Add a contract");
            System.out.println("4. Remove a vehicle");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    searchVehiclesMenu(scanner);
                    break;
                case 2:
                    addVehicleMenu(scanner);
                    break;
                case 3:
                    addContractMenu(scanner);
                    break;
                case 4:
                    removeVehicleMenu(scanner);
                    break;
                case 5:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        scanner.close();
    }

    public static String generateRandomVin() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString().toUpperCase().replaceAll("-", "");
        // Assuming VIN length is 17 characters, you can adjust this if needed
        String vin = randomUUIDString.substring(0, 17);
        return vin;
    }

    private static void addContractMenu(Scanner scanner) {
        System.out.print("Enter the VIN of the vehicle to add a contract: ");
        String vin = scanner.nextLine();

        System.out.println("\nSelect a contract type:");
        System.out.println("1. Sales Contract");
        System.out.println("2. Lease Contract");
        System.out.print("Enter your choice: ");
        int contractTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (contractTypeChoice) {
            case 1:
                addSalesContract(vin, scanner);
                break;
            case 2:
                addLeaseContract(vin, scanner);
                break;
            default:
                System.out.println("Invalid choice. Contract not added.");
                break;
        }
    }

    private static void addSalesContract(String vin, Scanner scanner) {

        System.out.print("Enter the sale date (YYYY-MM-DD): ");
        String saleDateStr = scanner.nextLine();
        LocalDate saleDate = LocalDate.parse(saleDateStr);

        System.out.print("Enter the price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        SalesDao.addSalesContract(vin, saleDate, price);

        System.out.println("Sales contract added successfully.");
    }

    private static void addLeaseContract(String vin, Scanner scanner) {

        System.out.print("Enter the lease start date (YYYY-MM-DD): ");
        String leaseStartDateStr = scanner.nextLine();
        LocalDate leaseStartDate = LocalDate.parse(leaseStartDateStr);

        System.out.print("Enter the lease end date (YYYY-MM-DD): ");
        String leaseEndDateStr = scanner.nextLine();
        LocalDate leaseEndDate = LocalDate.parse(leaseEndDateStr);

        System.out.print("Enter the monthly payment: ");
        double monthlyPayment = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        LeaseDao.addLeaseContract(vin, leaseStartDate, leaseEndDate, monthlyPayment);

        System.out.println("Lease contract added successfully.");
    }

    private static void searchVehiclesMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nSearch Vehicles:");
            System.out.println("1. By price range");
            System.out.println("2. By make/model");
            System.out.println("3. By year range");
            System.out.println("4. By color");
            System.out.println("5. By mileage range");
            System.out.println("6. By type");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    searchByPriceRange(scanner);
                    break;
                case 2:
                    searchByMakeAndModel(scanner);
                    break;
                case 3:
                    searchByYearRange(scanner);
                    break;
                case 4:
                    searchByColor(scanner);
                    break;
                case 5:
                    searchByMileageRange(scanner);
                    break;
                case 6:
                    searchByType(scanner);
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private static void searchByPriceRange(Scanner scanner) {
        System.out.print("Enter the minimum price: ");
        double minPrice = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter the maximum price: ");
        double maxPrice = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        List<CarDealership.Vehicles> vehicles = VehicleDao.searchByPriceRange(minPrice, maxPrice);
        displaySearchResults(vehicles);
    }

    private static void searchByMakeAndModel(Scanner scanner) {
        System.out.print("Enter the make: ");
        String make = scanner.nextLine();

        System.out.print("Enter the model: ");
        String model = scanner.nextLine();

        List<CarDealership.Vehicles> vehicles = VehicleDao.searchByMakeModel(make, model);
        displaySearchResults(vehicles);
    }

    private static void searchByYearRange(Scanner scanner) {
        System.out.print("Enter the minimum year: ");
        int minYear = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter the maximum year: ");
        int maxYear = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        List<CarDealership.Vehicles> vehicles = VehicleDao.searchByYearRange(minYear, maxYear);
        displaySearchResults(vehicles);
    }

    private static void searchByColor(Scanner scanner) {
        System.out.print("Enter the color: ");
        String color = scanner.nextLine();

        List<CarDealership.Vehicles> vehicles = VehicleDao.searchByColor(color);
        displaySearchResults(vehicles);
    }

    private static void searchByMileageRange(Scanner scanner) {
        System.out.print("Enter the minimum mileage: ");
        int minMileage = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter the maximum mileage: ");
        int maxMileage = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        List<CarDealership.Vehicles> vehicles = VehicleDao.searchByMileageRange(minMileage, maxMileage);
        displaySearchResults(vehicles);
    }

    private static void searchByType(Scanner scanner) {
        System.out.print("Enter the vehicle type: ");
        String type = scanner.nextLine();

        List<CarDealership.Vehicles> vehicles = VehicleDao.searchByType(type);
        displaySearchResults(vehicles);
    }

    private static void displaySearchResults(List<CarDealership.Vehicles> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        } else {
            System.out.println("\nSearch Results:");
            for (var vehicle : vehicles) {
                System.out.println(vehicle);
            }
        }
    }

    private static void addVehicleMenu(Scanner scanner) {

        String vin = generateRandomVin();

        System.out.print("Enter the make: ");
        String make = scanner.nextLine();

        System.out.print("Enter the model: ");
        String model = scanner.nextLine();

        System.out.print("Enter the year: ");
        int year = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter the color: ");
        String color = scanner.nextLine();

        System.out.print("Enter the mileage: ");
        int mileage = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter the price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Enter the type: ");
        String type = scanner.nextLine();

        System.out.print("Enter the dealership ID: ");
        int dealershipId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        VehicleDao.addVehicle(vin, make, model, year, false, color, type, mileage, price);
        InventoryDao.addVehicleToInventory(vin, dealershipId);

        System.out.println("Vehicle added successfully.");
    }

    private static void removeVehicleMenu(Scanner scanner) {
        System.out.print("Enter the VIN of the vehicle to remove: ");
        String vin = scanner.nextLine();

        InventoryDao.removeVehicleFromInventory(vin);
        VehicleDao.removeVehicle(vin);
        System.out.println("Vehicle removed successfully.");

    }
}
