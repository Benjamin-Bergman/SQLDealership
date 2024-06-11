/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import manifold.ext.props.rt.api.*;

import java.time.*;

/**
 * Represents a contract for a leased vehicle.
 */
public class LeaseContract extends BaseContract {
    /**
     * The expected ending value of this contract.
     */
    @val
    public double expectedEndingValue;
    /**
     * The lease fee of this contract.
     */
    @val
    public double leaseFee;

    @SuppressWarnings("MissingJavadoc")
    @override
    @val
    public int paymentLength = 36;


    /**
     * @param vehicleSold         The vehicle this contract is for
     * @param email               The email to contact the contracted party
     * @param customerName        The name of the contracted party
     * @param date                The date this contract was made
     * @param expectedEndingValue The expected ending value for this contract
     * @param leaseFee            The lease fee for this contract
     */
    public LeaseContract(Vehicle vehicleSold, String email, String customerName, LocalDate date, double expectedEndingValue, double leaseFee) {
        super(vehicleSold, email, customerName, date);
        this.expectedEndingValue = expectedEndingValue;
        this.leaseFee = leaseFee;
    }

    /**
     * Creates a new instance whose {@code date} is {@code LocalDate.now()}.
     *
     * @param vehicleSold         The vehicle this contract is for
     * @param email               The email to contact the contracted party
     * @param customerName        The name of the contracted party
     * @param expectedEndingValue The expected ending value for this contract
     * @param leaseFee            The lease fee for this contract
     */
    public LeaseContract(Vehicle vehicleSold, String email, String customerName, double expectedEndingValue, double leaseFee) {
        super(vehicleSold, email, customerName);
        this.expectedEndingValue = expectedEndingValue;
        this.leaseFee = leaseFee;
    }

    @Override
    public double getTotalPrice() {
        return vehicleSold.price() + leaseFee;
    }

    @Override
    public double getMonthlyPayment() {
        return totalPrice * 0.04;
    }
}
