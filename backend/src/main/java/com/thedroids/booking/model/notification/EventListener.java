package com.thedroids.booking.model.notification;

/**
 * Observer pattern for subscriber interface.
 * Implement this to react to system events.
 */
public interface EventListener {

    void onEvent(EventType eventType, String message);
}
