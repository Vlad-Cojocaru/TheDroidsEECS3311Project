package com.thedroids.booking.model.booking;

import com.thedroids.booking.model.booking.state.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private String id;
    private String clientId;
    private String consultantId;
    private String serviceId;
    private String timeSlotId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Transient
    private BookingState state;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Booking() {}

    public Booking(String id, String clientId, String consultantId,
                   String serviceId, String timeSlotId) {
        this.id = id;
        this.clientId = clientId;
        this.consultantId = consultantId;
        this.serviceId = serviceId;
        this.timeSlotId = timeSlotId;
        this.state = new RequestedState();
        this.status = BookingStatus.REQUESTED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PostLoad
    private void initState() {
        this.state = resolveState(this.status);
    }

    private BookingState resolveState(BookingStatus status) {
        return switch (status) {
            case REQUESTED -> new RequestedState();
            case CONFIRMED -> new ConfirmedState();
            case PENDING_PAYMENT -> new PendingPaymentState();
            case PAID -> new PaidState();
            case REJECTED -> new RejectedState();
            case CANCELLED -> new CancelledState();
            case COMPLETED -> new CompletedState();
        };
    }

    public void confirm() { getStateInternal().confirm(this); }
    public void reject() { getStateInternal().reject(this); }
    public void cancel() { getStateInternal().cancel(this); }
    public void markPendingPayment() { getStateInternal().markPendingPayment(this); }
    public void pay() { getStateInternal().pay(this); }
    public void complete() { getStateInternal().complete(this); }

    private BookingState getStateInternal() {
        if (state == null) { initState(); }
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
        this.status = state.getStatus();
        this.updatedAt = LocalDateTime.now();
    }

    public BookingStatus getStatus() { return status; }
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
