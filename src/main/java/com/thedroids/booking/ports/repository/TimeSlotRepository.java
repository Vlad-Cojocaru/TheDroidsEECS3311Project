package com.thedroids.booking.ports.repository;

import com.thedroids.booking.domain.availability.TimeSlot;

import java.util.List;
import java.util.Optional;

public interface TimeSlotRepository {
    void save(TimeSlot timeSlot);
    Optional<TimeSlot> findById(String id);
    List<TimeSlot> findByConsultantId(String consultantId);
    List<TimeSlot> findAvailableByConsultantId(String consultantId);
    void delete(String id);
}
