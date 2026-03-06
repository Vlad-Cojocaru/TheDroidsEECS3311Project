package com.thedroids.booking.infrastructure.persistence;

import com.thedroids.booking.domain.payment.PaymentMethod;
import com.thedroids.booking.ports.repository.PaymentMethodRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryPaymentMethodRepository implements PaymentMethodRepository {

    private Map<String, PaymentMethod> methods = new HashMap<>();

    @Override
    public void save(PaymentMethod method) {
        methods.put(method.getId(), method);
    }

    @Override
    public Optional<PaymentMethod> findById(String id) {
        return Optional.ofNullable(methods.get(id));
    }

    @Override
    public List<PaymentMethod> findByClientId(String clientId) {
        return methods.values().stream()
                .filter(m -> m.getClientId().equals(clientId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        methods.remove(id);
    }
}
