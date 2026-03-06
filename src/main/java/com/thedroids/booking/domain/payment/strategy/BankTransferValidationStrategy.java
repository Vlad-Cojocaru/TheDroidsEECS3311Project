package com.thedroids.booking.domain.payment.strategy;

import com.thedroids.booking.domain.payment.PaymentMethod;

import java.util.Map;

public class BankTransferValidationStrategy implements PaymentValidationStrategy {

    @Override
    public boolean validate(PaymentMethod paymentMethod) {
        Map<String, String> details = paymentMethod.getDetails();
        String accountNumber = details.get("accountNumber");
        String routingNumber = details.get("routingNumber");

        if (accountNumber == null || routingNumber == null) {
            return false;
        }

        // account number should be 8-12 digits
        if (!accountNumber.matches("\\d{8,12}")) {
            return false;
        }

        // routing number is always 9 digits
        return routingNumber.matches("\\d{9}");
    }
}
