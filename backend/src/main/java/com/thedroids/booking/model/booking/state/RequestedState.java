package com.thedroids.booking.model.booking.state;

import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;

public class RequestedState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        booking.setState(new ConfirmedState());
    }

    @Override
    public void reject(Booking booking) {
        booking.setState(new RejectedState());
    }

    @Override
    public void cancel(Booking booking) {
        booking.setState(new CancelledState());
    }

    @Override
    public void markPendingPayment(Booking booking) {
        throw new IllegalStateException("Cannot mark pending payment: booking is still in REQUESTED state.");
    }

    @Override
    public void pay(Booking booking) {
        throw new IllegalStateException("Cannot pay: booking is still in REQUESTED state.");
    }

    @Override
    public void complete(Booking booking) {
        throw new IllegalStateException("Cannot complete: booking is still in REQUESTED state.");
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.REQUESTED;
    }
}
