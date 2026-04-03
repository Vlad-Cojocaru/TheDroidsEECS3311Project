package com.thedroids.booking.model.payment.factory;

import com.thedroids.booking.model.payment.PaymentMethodType;
import com.thedroids.booking.model.payment.strategy.*;

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
