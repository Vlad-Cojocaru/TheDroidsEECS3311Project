package com.thedroids.booking.model.booking.state;

import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;

public class CompletedState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        throw new IllegalStateException("Cannot confirm: booking is already completed.");
    }

    @Override
    public void reject(Booking booking) {
        throw new IllegalStateException("Cannot reject: booking is already completed.");
    }

    @Override
    public void cancel(Booking booking) {
        throw new IllegalStateException("Cannot cancel: booking is already completed.");
    }

    @Override
    public void markPendingPayment(Booking booking) {
        throw new IllegalStateException("Cannot process: booking is already completed.");
    }

    @Override
    public void pay(Booking booking) {
        throw new IllegalStateException("Cannot pay: booking is already completed.");
    }

    @Override
    public void complete(Booking booking) {
        throw new IllegalStateException("Booking is already completed.");
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.COMPLETED;
    }
}
