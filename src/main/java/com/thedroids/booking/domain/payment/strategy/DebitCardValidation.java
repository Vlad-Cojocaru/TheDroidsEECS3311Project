package com.thedroids.booking.domain.payment.strategy;

import com.thedroids.booking.domain.payment.PaymentMethod;

import java.util.Map;

// same validation rules as credit card per the spec
public class DebitCardValidationStrategy implements PaymentValidationStrategy {

    @Override
    public boolean validate(PaymentMethod paymentMethod) {
        Map<String, String> details = paymentMethod.getDetails();

        String cardNumber = details.get("cardNumber");
        String expiryDate = details.get("expiryDate");
        String cvv = details.get("cvv");

        // all fields required
        if (cardNumber == null || expiryDate == null || cvv == null) {
            return false;
        }

        // check card number is 16 digits
        if (!cardNumber.matches("\\d{16}")) {
            return false;
        }

        // check expiry format MM/YY
        if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return false;
        }

        // cvv should be 3 or 4 digits
        if (!cvv.matches("\\d{3,4}")) {
            return false;
        }
    }
}
