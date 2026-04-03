package com.thedroids.booking.model.notification;

public class SMSNotificationListener implements EventListener {

    private final String phoneNumber;

    public SMSNotificationListener(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void onEvent(EventType eventType, String message) {
        System.out.println("  [SMS -> " + phoneNumber + "] " + eventType + ": " + message);
    }
}
