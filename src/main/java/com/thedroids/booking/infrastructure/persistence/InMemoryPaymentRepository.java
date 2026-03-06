package com.thedroids.booking.infrastructure.persistence;

import com.thedroids.booking.domain.payment.Payment;
import com.thedroids.booking.ports.repository.PaymentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryPaymentRepository implements PaymentRepository {

    private Map<String, Payment> payments = new HashMap<>();

    @Override
    public void save(Payment payment) {
        payments.put(payment.getId(), payment);
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public List<Payment> findByClientId(String clientId) {
        return payments.values().stream()
                .filter(p -> p.getClientId().equals(clientId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Payment> findByBookingId(String bookingId) {
        return payments.values().stream()
                .filter(p -> p.getBookingId().equals(bookingId))
                .findFirst();
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }
}
