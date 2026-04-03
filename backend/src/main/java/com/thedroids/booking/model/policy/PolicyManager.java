package com.thedroids.booking.model.policy;

/**
 * Singleton pattern to ensures a single system-wide PolicyManager instance.
 * Uses thread-safe double-checked locking (as taught in Week 3 lectures).
 */
public class PolicyManager {

    private static volatile PolicyManager instance;
    private SystemPolicy currentPolicy;

    private PolicyManager() {
        this.currentPolicy = new SystemPolicy();
    }

    public static PolicyManager getInstance() {
        if (instance == null) {
            synchronized (PolicyManager.class) {
                if (instance == null) {
                    instance = new PolicyManager();
                }
            }
        }
        return instance;
    }

    public SystemPolicy getPolicy() {
        return currentPolicy;
    }

    public void updatePolicy(SystemPolicy policy) {
        this.currentPolicy = policy;
    }
}
