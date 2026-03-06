package com.thedroids.booking.application;

import com.thedroids.booking.domain.booking.Booking;
import com.thedroids.booking.domain.booking.BookingStatus;
import com.thedroids.booking.domain.notification.EventManager;
import com.thedroids.booking.domain.notification.EventType;
import com.thedroids.booking.domain.payment.*;
import com.thedroids.booking.domain.payment.factory.PaymentValidationFactory;
import com.thedroids.booking.domain.payment.strategy.PaymentValidationStrategy;
import com.thedroids.booking.domain.service.ConsultingService;
import com.thedroids.booking.ports.repository.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaymentService {

    private PaymentRepository paymentRepository;
    private PaymentMethodRepository paymentMethodRepository;
    private BookingRepository bookingRepository;
    private ServiceRepository serviceRepository;
    private EventManager eventManager;

    public PaymentService(
                    PaymentRepository paymentRepository,
                    PaymentMethodRepository paymentMethodRepository,
                    BookingRepository bookingRepository,
                    ServiceRepository serviceRepository,
                    EventManager eventManager) {
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.eventManager = eventManager;
    }

    // UC5: process payment for a confirmed booking
    public Payment processPayment(String bookingId, String paymentMethodId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Booking must be in PENDING_PAYMENT state. Current: " + booking.getStatus());
        }

        PaymentMethod method = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found: " + paymentMethodId));

        // use the factory to get the right validation strategy
        PaymentValidationStrategy strategy = PaymentValidationFactory.createStrategy(method.getType());
        if (!strategy.validate(method)) {
            throw new IllegalArgumentException("Invalid payment method details for " + method.getType());
        }

        ConsultingService service = serviceRepository.findById(booking.getServiceId())
                .orElseThrow(() -> new IllegalStateException("Service not found for booking."));

        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                bookingId, booking.getClientId(),
                paymentMethodId, service.getBasePrice());

        // simulate payment processing delay
        System.out.println("  Processing payment..");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        booking.pay();
        bookingRepository.save(booking);

        eventManager.notify(EventType.PAYMENT_PROCESSED,
                "Payment of $" + service.getBasePrice()
                + " processed. Transaction: " + payment.getTransactionId());

        return payment;
    }

    // UC6: add a new payment method for a client
    public PaymentMethod addPaymentMethod(String clientId, PaymentMethodType type,
                                          Map<String, String> details) {
        PaymentMethod method = new PaymentMethod(
                UUID.randomUUID().toString(), type, clientId, details);

        PaymentValidationStrategy strategy = PaymentValidationFactory.createStrategy(type);
        if (!strategy.validate(method)) {
            throw new IllegalArgumentException("Invalid details for payment method type: " + type);
        }

        paymentMethodRepository.save(method);
        return method;
    }

    // UC6: get all payment methods for a client
    public List<PaymentMethod> getPaymentMethods(String clientId) {
        return paymentMethodRepository.findByClientId(clientId);
    }

    // UC6: remove a payment method
    public void removePaymentMethod(String paymentMethodId) {
        paymentMethodRepository.delete(paymentMethodId);
    }

    // UC7: view payment history for a client
    public List<Payment> getPaymentHistory(String clientId) {
        return paymentRepository.findByClientId(clientId);
    }
}
