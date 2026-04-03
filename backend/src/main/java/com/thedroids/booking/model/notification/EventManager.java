package com.thedroids.booking.model.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observer pattern for publisher/subject.
 * Manages subscriptions and broadcasts events to all registered listeners.
 */
public class EventManager {

    private final Map<EventType, List<EventListener>> listeners = new HashMap<>();

    public void subscribe(EventType eventType, EventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(EventType eventType, EventListener listener) {
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void notify(EventType eventType, String message) {
        List<EventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.onEvent(eventType, message);
            }
        }
    }
}
