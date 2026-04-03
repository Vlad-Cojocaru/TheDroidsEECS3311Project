package com.thedroids.booking.model.payment.strategy;

import com.thedroids.booking.model.payment.PaymentMethod;

public class PayPalValidationStrategy implements PaymentValidationStrategy {

    @Override
    public boolean validate(PaymentMethod paymentMethod) {
        String email = paymentMethod.getDetails().get("email");
        if (email == null) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
