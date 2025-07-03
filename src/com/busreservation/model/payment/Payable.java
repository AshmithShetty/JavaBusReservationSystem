package com.busreservation.model.payment;

/**
 * An interface defining the contract for a payment method.
 */
public interface Payable {
    /**
     * Processes a payment for a given amount.
     * @param amount The total amount to be paid.
     * @return true if the payment is successful, false otherwise.
     */
    boolean processPayment(double amount);
}