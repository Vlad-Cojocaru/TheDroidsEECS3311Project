package com.thedroids.booking.model.availability;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
public class TimeSlot {

    @Id
    private String id;
    private String consultantId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;

    protected TimeSlot() {}

    public TimeSlot(String id, String consultantId, LocalDate date,
                    LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.consultantId = consultantId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = true;
    }

    public String getId() { return id; }
    public String getConsultantId() { return consultantId; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return date + " " + startTime + "-" + endTime + (available ? " [AVAILABLE]" : " [BOOKED]");
    }
}
