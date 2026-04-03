package com.thedroids.booking.model.booking.state;

import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;

public class CancelledState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        throw new IllegalStateException("Cannot confirm: booking has been cancelled.");
    }

    @Override
    public void reject(Booking booking) {
        throw new IllegalStateException("Cannot reject: booking has been cancelled.");
    }

    @Override
    public void cancel(Booking booking) {
        throw new IllegalStateException("Booking is already cancelled.");
    }

    @Override
    public void markPendingPayment(Booking booking) {
        throw new IllegalStateException("Cannot process: booking has been cancelled.");
    }

    @Override
    public void pay(Booking booking) {
        throw new IllegalStateException("Cannot pay: booking has been cancelled.");
    }

    @Override
    public void complete(Booking booking) {
        throw new IllegalStateException("Cannot complete: booking has been cancelled.");
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.CANCELLED;
    }
}
