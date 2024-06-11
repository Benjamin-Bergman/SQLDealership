/*
 * Copyright (c) Benjamin Bergman 2024.
 */

package com.pluralsight;

import manifold.ext.props.rt.api.*;

import java.time.*;

/**
 * Base class for simple implementations of Contracts.
 */
abstract class BaseContract implements Contract {
    /**
     * The vehicle this contract is for.
     */
    @val
    @override
    public final Vehicle vehicleSold;
    /**
     * The date this contract was made
     */
    @val
    public final LocalDate date;
    /**
     * The email to contact the contracted party.
     */
    @val
    public final String email;
    /**
     * The name of the contracted party.
     */
    @val
    public final String customerName;

    /**
     * @param vehicleSold  The vehicle this contract is for
     * @param email        The email to contact the contracted party
     * @param customerName The name of the contracted party
     * @param date         The date this contract was made
     */
    protected BaseContract(Vehicle vehicleSold, String email, String customerName, LocalDate date) {
        this.vehicleSold = vehicleSold;
        this.date = date;
        this.email = email;
        this.customerName = customerName;
    }

    /**
     * Creates a new instance whose {@code date} is {@code LocalDate.now()}.
     *
     * @param vehicleSold  The vehicle this contract is for
     * @param email        The email to contact the contracted party
     * @param customerName The name of the contracted party
     */
    protected BaseContract(Vehicle vehicleSold, String email, String customerName) {
        this(vehicleSold, email, customerName, LocalDate.now());
    }
}
