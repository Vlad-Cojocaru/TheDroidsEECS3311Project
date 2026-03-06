package com.thedroids.booking.infrastructure.persistence;

import com.thedroids.booking.domain.booking.Booking;
import com.thedroids.booking.ports.repository.BookingRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> store = new HashMap<>();

    @Override
    public void save(Booking booking) {
        store.put(booking.getId(), booking);
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Booking> findByClientId(String clientId) {
        return store.values().stream()
                .filter(b -> b.getClientId().equals(clientId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByConsultantId(String consultantId) {
        return store.values().stream()
                .filter(b -> b.getConsultantId().equals(consultantId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(store.values());
    }
}
