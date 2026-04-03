package com.thedroids.booking.repository;

import com.thedroids.booking.model.payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {
    List<PaymentMethod> findByClientId(String clientId);
}
