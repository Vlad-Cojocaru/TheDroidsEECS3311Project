package com.thedroids.booking.model.notification;

public class EmailNotificationListener implements EventListener {

    private final String recipientEmail;

    public EmailNotificationListener(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void onEvent(EventType eventType, String message) {
        System.out.println("  [EMAIL -> " + recipientEmail + "] " + eventType + ": " + message);
    }
}
