package com.thedroids.booking.model.payment.strategy;

import com.thedroids.booking.model.payment.PaymentMethod;

import java.util.Map;

public class CreditCardValidationStrategy implements PaymentValidationStrategy {

    @Override
    public boolean validate(PaymentMethod paymentMethod) {
        Map<String, String> details = paymentMethod.getDetails();
        String cardNumber = details.get("cardNumber");
        String expiryDate = details.get("expiryDate");
        String cvv = details.get("cvv");

        if (cardNumber == null || expiryDate == null || cvv == null) {
            return false;
        }

        if (!cardNumber.matches("\\d{16}")) {
            return false;
        }

        if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }

        if (!cvv.matches("\\d{3,4}")) {
            return false;
        }

        return true;
    }
}
