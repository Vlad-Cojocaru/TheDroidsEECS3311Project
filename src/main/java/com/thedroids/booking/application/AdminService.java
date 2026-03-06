package com.thedroids.booking.application;

import com.thedroids.booking.domain.notification.EventManager;
import com.thedroids.booking.domain.notification.EventType;
import com.thedroids.booking.domain.policy.PolicyManager;
import com.thedroids.booking.domain.policy.SystemPolicy;
import com.thedroids.booking.domain.user.Consultant;
import com.thedroids.booking.domain.user.User;
import com.thedroids.booking.ports.repository.UserRepository;

import java.util.List;

/**
 * Handles UC11 (Approve Consultant Registration) and UC12 (Define System Policies).
 */
public class AdminService {

    private final UserRepository userRepository;
    private final EventManager eventManager;

    public AdminService(UserRepository userRepository, EventManager eventManager) {
        this.userRepository = userRepository;
        this.eventManager = eventManager;
    }

    /** UC11 — Approve a consultant who is waiting for admin approval.  */
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

    /** UC11 — Reject a pending consultant registration. */
    public void rejectConsultant(String consultantId) {
        User user = userRepository.findById(consultantId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + consultantId));

        if (!(user instanceof Consultant consultant)) {
            throw new IllegalArgumentException("User is not a consultant.");
        }

        eventManager.notify(EventType.CONSULTANT_REJECTED,
                "Consultant '" + consultant.getName() + "' registration rejected.");

        userRepository.delete(consultantId);
    }

    /** UC11 — Return a list of consultants who are still waiting for approval. */
    public List<Consultant> getPendingConsultants() {
        return userRepository.findAll().stream()
                .filter(u -> u instanceof Consultant c && !c.isApproved())
                .map(u -> (Consultant) u)
                .toList();
    }

    /** UC12 — Get the current system policy configuration. */
    public SystemPolicy getCurrentPolicy() {
        return PolicyManager.getInstance().getPolicy();
    }

    /** UC12 — Update the system policy settings. */
    public void updatePolicy(SystemPolicy policy) {
        PolicyManager.getInstance().updatePolicy(policy);
    }
}
