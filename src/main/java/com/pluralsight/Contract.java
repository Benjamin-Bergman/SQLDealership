/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import manifold.ext.props.rt.api.*;

/**
 * Represents a contract for a sold/leased {@link Vehicle}.
 */
public interface Contract {
    /**
     * The total price of this contract.
     */
    @val
    double totalPrice;
    /**
     * The monthly payment for this contract.
     */
    @val
    double monthlyPayment;
    /**
     * The term lengths of this contract in months.
     */
    @val
    int paymentLength;
    /**
     * The vehicle sold for this contract.
     */
    @val
    Vehicle vehicleSold;
}
