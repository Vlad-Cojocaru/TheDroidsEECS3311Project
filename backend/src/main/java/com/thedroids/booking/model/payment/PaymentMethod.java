package com.thedroids.booking.model.payment;

import jakarta.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private PaymentMethodType type;

    private String clientId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "payment_method_details", joinColumns = @JoinColumn(name = "payment_method_id"))
    @MapKeyColumn(name = "detail_key")
    @Column(name = "detail_value")
    private Map<String, String> details = new HashMap<>();

    protected PaymentMethod() {}

    public PaymentMethod(String id, PaymentMethodType type, String clientId,
                         Map<String, String> details) {
        this.id = id;
        this.type = type;
        this.clientId = clientId;
        this.details = details != null ? details : new HashMap<>();
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
