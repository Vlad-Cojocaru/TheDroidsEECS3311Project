package com.thedroids.booking.service;

import com.thedroids.booking.model.notification.EventManager;
import com.thedroids.booking.model.notification.EventType;
import com.thedroids.booking.model.policy.SystemPolicy;
import com.thedroids.booking.model.user.Consultant;
import com.thedroids.booking.model.user.User;
import com.thedroids.booking.repository.PolicyRepository;
import com.thedroids.booking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final EventManager eventManager;
    private final PolicyRepository policyRepository;

    public AdminService(UserRepository userRepository, EventManager eventManager,
                        PolicyRepository policyRepository) {
        this.userRepository = userRepository;
        this.eventManager = eventManager;
        this.policyRepository = policyRepository;
    }

    public void approveConsultant(String consultantId) {
        User user = userRepository.findById(consultantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + consultantId));

        if (!(user instanceof Consultant consultant)) {
            throw new IllegalArgumentException("User is not a consultant.");
        }

        consultant.setApproved(true);
        userRepository.save(consultant);

        eventManager.notify(EventType.CONSULTANT_APPROVED,
                "Consultant '" + consultant.getName() + "' has been approved.");
    }

    public void rejectConsultant(String consultantId) {
        User user = userRepository.findById(consultantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + consultantId));

        if (!(user instanceof Consultant consultant)) {
            throw new IllegalArgumentException("User is not a consultant.");
        }

        eventManager.notify(EventType.CONSULTANT_REJECTED,
                "Consultant '" + consultant.getName() + "' registration rejected.");

        userRepository.deleteById(consultantId);
    }

    public List<Consultant> getPendingConsultants() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof Consultant c && !c.isApproved())
                .map(u -> (Consultant) u)
                .toList();
    }

    public SystemPolicy getCurrentPolicy() {
        return policyRepository.findAll().stream().findFirst()
                .orElseGet(() -> policyRepository.save(new SystemPolicy()));
    }

    public void updatePolicy(SystemPolicy policy) {
        SystemPolicy existing = getCurrentPolicy();
        existing.setCancellationWindowHours(policy.getCancellationWindowHours());
        existing.setCancellationFeePercent(policy.getCancellationFeePercent());
        existing.setRefundPercent(policy.getRefundPercent());
        existing.setNotificationsEnabled(policy.isNotificationsEnabled());
        policyRepository.save(existing);
    }
}
