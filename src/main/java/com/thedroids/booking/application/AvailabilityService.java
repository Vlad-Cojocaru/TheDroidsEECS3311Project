package com.thedroids.booking.application;

import com.thedroids.booking.domain.availability.TimeSlot;
import com.thedroids.booking.ports.repository.TimeSlotRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


// Handles UC8: Manage consultant availability (add/remove time slots)
 
public class AvailabilityService {

    private final TimeSlotRepository timeSlotRepository;

    public AvailabilityService(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public TimeSlot addTimeSlot(String consultantId, LocalDate date,
                                LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        TimeSlot slot = new TimeSlot(
                UUID.randomUUID().toString(),
                consultantId, date, startTime, endTime);
        timeSlotRepository.save(slot);
        return slot;
    }

    public void removeTimeSlot(String timeSlotId) {
        timeSlotRepository.delete(timeSlotId);
    }

    public List<TimeSlot> getAvailableSlots(String consultantId) {
        return timeSlotRepository.findAvailableByConsultantId(consultantId);
    }

    public List<TimeSlot> getAllSlots(String consultantId) {
        return timeSlotRepository.findByConsultantId(consultantId);
    }
}
