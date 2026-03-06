package com.thedroids.booking.domain.booking;

import com.thedroids.booking.domain.booking.state.BookingState;
import com.thedroids.booking.domain.booking.state.RequestedState;

import java.time.LocalDateTime;

/**
 * Context class for the State pattern.
 * Delegates all lifecycle operations to the current BookingState.
 */
public class Booking {

    private final String id;
    private final String clientId;
    private final String consultantId;
    private final String serviceId;
    private final String timeSlotId;
    private BookingState state;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Booking(String id, String clientId, String consultantId,
                   String serviceId, String timeSlotId) {
        this.id = id;
        this.clientId = clientId;
        this.consultantId = consultantId;
        this.serviceId = serviceId;
        this.timeSlotId = timeSlotId;
        this.state = new RequestedState();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void confirm() { state.confirm(this); }
    public void reject() { state.reject(this); }
    public void cancel() { state.cancel(this); }
    public void markPendingPayment() { state.markPendingPayment(this); }
    public void pay() { state.pay(this); }
    public void complete() { state.complete(this); }

    public void setState(BookingState state) {
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }

    public BookingStatus getStatus() { return state.getStatus(); }
    public String getId() { return id; }
    public String getClientId() { return clientId; }
    public String getConsultantId() { return consultantId; }
    public String getServiceId() { return serviceId; }
    public String getTimeSlotId() { return timeSlotId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return String.format("Booking{id='%s', status=%s, created=%s}", id, getStatus(), createdAt);
    }
}
