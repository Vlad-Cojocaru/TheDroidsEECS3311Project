package com.thedroids.booking.repository;

import com.thedroids.booking.model.policy.SystemPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<SystemPolicy, Long> {
}
