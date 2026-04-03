package com.thedroids.booking.model.payment.strategy;

import com.thedroids.booking.model.payment.PaymentMethod;

public interface PaymentValidationStrategy {

    boolean validate(PaymentMethod paymentMethod);
}
