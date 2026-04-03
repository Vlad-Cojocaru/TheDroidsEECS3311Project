package com.thedroids.booking.repository;

import com.thedroids.booking.model.availability.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {
    List<TimeSlot> findByConsultantId(String consultantId);
    List<TimeSlot> findByConsultantIdAndAvailableTrue(String consultantId);
}
