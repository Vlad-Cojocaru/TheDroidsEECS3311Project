package com.thedroids.booking.config;

import com.thedroids.booking.model.notification.EmailNotificationListener;
import com.thedroids.booking.model.notification.EventManager;
import com.thedroids.booking.model.notification.EventType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventManagerConfig {

    @Bean
    public EventManager eventManager() {
        EventManager em = new EventManager();
        EmailNotificationListener listener = new EmailNotificationListener("system@thedroids.com");
        for (EventType type : EventType.values()) {
            em.subscribe(type, listener);
        }
        return em;
    }
}
