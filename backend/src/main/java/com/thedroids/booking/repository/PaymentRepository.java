package com.thedroids.booking.repository;

import com.thedroids.booking.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByClientId(String clientId);
    Optional<Payment> findByBookingId(String bookingId);
}
