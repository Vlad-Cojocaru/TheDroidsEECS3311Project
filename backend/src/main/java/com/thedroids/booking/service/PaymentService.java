package com.thedroids.booking.service;

import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;
import com.thedroids.booking.model.notification.EventManager;
import com.thedroids.booking.model.notification.EventType;
import com.thedroids.booking.model.payment.*;
import com.thedroids.booking.model.payment.factory.PaymentValidationFactory;
import com.thedroids.booking.model.payment.strategy.PaymentValidationStrategy;
import com.thedroids.booking.model.service.ConsultingService;
import com.thedroids.booking.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final EventManager eventManager;

    public PaymentService(PaymentRepository paymentRepository,
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

    public Payment processPayment(String bookingId, String paymentMethodId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Booking must be in PENDING_PAYMENT state. Current: " + booking.getStatus());
        }

        PaymentMethod method = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("Payment method not found: " + paymentMethodId));

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

    public List<PaymentMethod> getPaymentMethods(String clientId) {
        return paymentMethodRepository.findByClientId(clientId);
    }

    public void removePaymentMethod(String paymentMethodId) {
        paymentMethodRepository.deleteById(paymentMethodId);
    }

    public List<Payment> getPaymentHistory(String clientId) {
        return paymentRepository.findByClientId(clientId);
    }
}
