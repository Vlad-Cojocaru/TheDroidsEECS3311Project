package com.thedroids.booking.ports.repository;

import com.thedroids.booking.domain.payment.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository {
    void save(PaymentMethod method);
    Optional<PaymentMethod> findById(String id);
    List<PaymentMethod> findByClientId(String clientId);
    void delete(String id);
}
