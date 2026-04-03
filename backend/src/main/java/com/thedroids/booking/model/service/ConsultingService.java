package com.thedroids.booking.model.service;

import jakarta.persistence.*;

@Entity
@Table(name = "consulting_services")
public class ConsultingService {

    @Id
    private String id;
    private String name;
    private String description;
    private String type;
    private int durationMinutes;
    private double basePrice;
    private String consultantId;

    protected ConsultingService() {}

    public ConsultingService(String id, String name, String description, String type,
                             int durationMinutes, double basePrice, String consultantId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.durationMinutes = durationMinutes;
        this.basePrice = basePrice;
        this.consultantId = consultantId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getDurationMinutes() { return durationMinutes; }
    public double getBasePrice() { return basePrice; }
    public String getConsultantId() { return consultantId; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    @Override
    public String toString() {
        return String.format("%-20s | %-15s | %3d min | $%.2f", name, type, durationMinutes, basePrice);
    }
}
