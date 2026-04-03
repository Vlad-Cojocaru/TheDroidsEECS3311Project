package com.thedroids.booking.model.booking.state;

import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;

public class PaidState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        throw new IllegalStateException("Booking is already paid.");
    }

    @Override
    public void reject(Booking booking) {
        throw new IllegalStateException("Cannot reject a paid booking.");
    }

    @Override
    public void cancel(Booking booking) {
        booking.setState(new CancelledState());
    }

    @Override
    public void markPendingPayment(Booking booking) {
        throw new IllegalStateException("Booking is already paid.");
    }

    @Override
    public void pay(Booking booking) {
        throw new IllegalStateException("Booking is already paid.");
    }

    @Override
    public void complete(Booking booking) {
        booking.setState(new CompletedState());
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.PAID;
    }
}
