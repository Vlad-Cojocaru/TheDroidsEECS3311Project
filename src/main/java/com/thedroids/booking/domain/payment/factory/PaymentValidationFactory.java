package com.thedroids.booking.domain.payment.factory;

import com.thedroids.booking.domain.payment.PaymentMethodType;
import com.thedroids.booking.domain.payment.strategy.*;

// factory that returns the right validation strategy based on payment type
public class PaymentValidationFactory {

    public static PaymentValidationStrategy createStrategy(PaymentMethodType type) {
        return switch (type) {
            case CREDIT_CARD -> new CreditCardValidationStrategy();
            case DEBIT_CARD -> new DebitCardValidationStrategy();
            case PAYPAL -> new PayPalValidationStrategy();
            case BANK_TRANSFER -> new BankTransferValidationStrategy();
        };
    }
}
