DROP DATABASE IF EXISTS car_dealership;

CREATE DATABASE car_dealership;

USE car_dealership;

DROP TABLE IF EXISTS dealerships;
CREATE TABLE dealerships (
    dealership_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    address VARCHAR(50),
    phone VARCHAR(12)
);

DROP TABLE IF EXISTS vehicles;
CREATE TABLE vehicles (
    vin INT NOT NULL PRIMARY KEY,
    year INT NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    vehicleType VARCHAR(20) NOT NULL,
    color VARCHAR(20) NOT NULL,
    odometerReading INT NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    sold BOOLEAN NOT NULL DEFAULT FALSE
);

DROP TABLE IF EXISTS inventory;
CREATE TABLE inventory (
    dealership_id INT NOT NULL,
    vin INT NOT NULL,
    FOREIGN KEY (dealership_id) REFERENCES dealerships(dealership_id),
    FOREIGN KEY (vin) REFERENCES vehicles(vin)
);

DROP TABLE IF EXISTS sales_contracts;
CREATE TABLE sales_contracts (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vin INT NOT NULL,
    sales_tax DECIMAL(12, 2) NOT NULL,
    recording_fee DECIMAL(12, 2) NOT NULL,
    processing_fee DECIMAL(12, 2) NOT NULL,
    financed BOOLEAN NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (vin) REFERENCES vehicles(vin)
);

DROP TABLE IF EXISTS lease_contracts;
CREATE TABLE lease_contracts (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    vin INT NOT NULL,
    expected_ending_value DECIMAL(12, 2) NOT NULL,
    lease_fee DECIMAL(12, 2) NOT NULL,
    payment_term INT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (vin) REFERENCES vehicles(vin)
);