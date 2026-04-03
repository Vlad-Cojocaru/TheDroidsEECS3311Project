package com.thedroids.booking.repository;

import com.thedroids.booking.model.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByClientId(String clientId);
    List<Booking> findByConsultantId(String consultantId);
}
