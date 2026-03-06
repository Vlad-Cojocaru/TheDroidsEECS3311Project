package com.thedroids.booking.ports.repository;

import com.thedroids.booking.domain.payment.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(String id);
    List<Payment> findByClientId(String clientId);
    Optional<Payment> findByBookingId(String bookingId);
    List<Payment> findAll();
}
