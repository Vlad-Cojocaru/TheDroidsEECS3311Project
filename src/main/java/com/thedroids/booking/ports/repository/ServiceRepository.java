package com.thedroids.booking.ports.repository;

import com.thedroids.booking.domain.service.ConsultingService;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
    void save(ConsultingService service);
    Optional<ConsultingService> findById(String id);
    List<ConsultingService> findAll();
    List<ConsultingService> findByConsultantId(String consultantId);
}
