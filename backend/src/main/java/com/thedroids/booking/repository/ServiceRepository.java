package com.thedroids.booking.repository;

import com.thedroids.booking.model.service.ConsultingService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceRepository extends JpaRepository<ConsultingService, String> {
    List<ConsultingService> findByConsultantId(String consultantId);
}
