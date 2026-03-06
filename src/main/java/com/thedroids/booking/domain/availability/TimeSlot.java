package com.thedroids.booking.domain.availability;

import java.time.LocalDate;
import java.time.LocalTime;

public class TimeSlot {

    private final String id;
    private final String consultantId;
    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private boolean available;

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
