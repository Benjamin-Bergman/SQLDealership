/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import manifold.ext.props.rt.api.*;

import java.time.*;

public class SalesContract extends BaseContract {
    /**
     * The sales tax for this contract.
     */
    @val
    public double salesTax;
    /**
     * The recording fee for this contract.
     */
    @val
    public double recordingFee;
    /**
     * The processing fee for this contract.
     */
    @val
    public double processingFee;
    /**
     * {@code true} is this contract was financed.
     */
    @val
    public boolean financed;

    /**
     * @param vehicleSold   The vehicle this contract is for
     * @param email         The email to contact the contracted party
     * @param customerName  The name of the contracted party
     * @param date          The date this contract was made
     * @param salesTax      The sales tax for this contract
     * @param recordingFee  The recording fee for this contract
     * @param processingFee The processing fee for this contract
     * @param financed      {@code true} is this contract was financed
     */
    public SalesContract(Vehicle vehicleSold, String email, String customerName, LocalDate date,
                         double salesTax, double recordingFee, double processingFee, boolean financed) {
        super(vehicleSold, email, customerName, date);
        this.salesTax = salesTax;
        this.recordingFee = recordingFee;
        this.processingFee = processingFee;
        this.financed = financed;
    }

    /**
     * Creates a new instance whose {@code date} is {@code LocalDate.now()}.
     *
     * @param vehicleSold   The vehicle this contract is for
     * @param email         The email to contact the contracted party
     * @param customerName  The name of the contracted party
     * @param salesTax      The sales tax for this contract
     * @param recordingFee  The recording fee for this contract
     * @param processingFee The processing fee for this contract
     * @param financed      {@code true} is this contract was financed
     */
    public SalesContract(Vehicle vehicleSold, String email, String customerName,
                         double salesTax, double recordingFee, double processingFee, boolean financed) {
        super(vehicleSold, email, customerName);
        this.salesTax = salesTax;
        this.recordingFee = recordingFee;
        this.processingFee = processingFee;
        this.financed = financed;
    }

    @Override
    public double getTotalPrice() {
        return vehicleSold.price() * (1 + salesTax) + recordingFee + processingFee;
    }

    @Override
    public double getMonthlyPayment() {
        return financed
            ? totalPrice * (totalPrice > 10_000 ? 0.0425 : 0.0525)
            : 0;
    }

    @Override
    public int getPaymentLength() {
        return financed ?
            (totalPrice > 10_000 ? 48 : 24)
            : 0;
    }
}
