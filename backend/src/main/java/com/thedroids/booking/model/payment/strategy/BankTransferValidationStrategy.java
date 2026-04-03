package com.thedroids.booking.model.payment.strategy;

import com.thedroids.booking.model.payment.PaymentMethod;

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

        if (!accountNumber.matches("\\d{8,12}")) {
            return false;
        }

        return routingNumber.matches("\\d{9}");
    }
}
