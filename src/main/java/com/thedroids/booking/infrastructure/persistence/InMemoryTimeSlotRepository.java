package com.thedroids.booking.infrastructure.persistence;

import com.thedroids.booking.domain.availability.TimeSlot;
import com.thedroids.booking.ports.repository.TimeSlotRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTimeSlotRepository implements TimeSlotRepository {

    private final Map<String, TimeSlot> store = new HashMap<>();

    @Override
    public void save(TimeSlot timeSlot) {
        store.put(timeSlot.getId(), timeSlot);
    }

    @Override
    public Optional<TimeSlot> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<TimeSlot> findByConsultantId(String consultantId) {
        return store.values().stream()
                .filter(t -> t.getConsultantId().equals(consultantId))
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlot> findAvailableByConsultantId(String consultantId) {
        return store.values().stream()
                .filter(t -> t.getConsultantId().equals(consultantId) && t.isAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        store.remove(id);
    }
}
