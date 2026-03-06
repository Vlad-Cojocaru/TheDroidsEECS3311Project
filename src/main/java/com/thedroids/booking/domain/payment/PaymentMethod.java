package com.thedroids.booking.domain.payment;

import java.util.Map;

public class PaymentMethod {

    private String id;
    private PaymentMethodType type;
    private String clientId;
    private Map<String, String> details;

    public PaymentMethod(String id, PaymentMethodType type, String clientId,
                         Map<String, String> details) {
        this.id = id;
        this.type = type;
        this.clientId = clientId;
        this.details = details;
    }

    public String getId() { return id; }
    public PaymentMethodType getType() { return type; }
    public String getClientId() { return clientId; }
    public Map<String, String> getDetails() { return details; }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return type + " (id=" + id + ")";
    }
}
