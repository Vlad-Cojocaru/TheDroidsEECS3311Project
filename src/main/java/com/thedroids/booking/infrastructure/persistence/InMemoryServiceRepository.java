package com.thedroids.booking.infrastructure.persistence;

import com.thedroids.booking.domain.service.ConsultingService;
import com.thedroids.booking.ports.repository.ServiceRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryServiceRepository implements ServiceRepository {

    private final Map<String, ConsultingService> store = new HashMap<>();

    @Override
    public void save(ConsultingService service) {
        store.put(service.getId(), service);
    }

    @Override
    public Optional<ConsultingService> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<ConsultingService> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<ConsultingService> findByConsultantId(String consultantId) {
        return store.values().stream()
                .filter(s -> s.getConsultantId().equals(consultantId))
                .collect(Collectors.toList());
    }
}
