package com.thedroids.booking.domain.payment.strategy;

import com.thedroids.booking.domain.payment.PaymentMethod;

// Strategy interface, each payment type has its own validation
public interface PaymentValidationStrategy {

    boolean validate(PaymentMethod paymentMethod);
}
