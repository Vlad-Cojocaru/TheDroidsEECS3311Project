package com.thedroids.booking.domain.booking.state;

import com.thedroids.booking.domain.booking.Booking;
import com.thedroids.booking.domain.booking.BookingStatus;

public class ConfirmedState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        throw new IllegalStateException("Booking is already confirmed.");
    }

    @Override
    public void reject(Booking booking) {
        throw new IllegalStateException("Cannot reject: booking is already confirmed.");
    }

    @Override
    public void cancel(Booking booking) {
        booking.setState(new CancelledState());
    }

    @Override
    public void markPendingPayment(Booking booking) {
        booking.setState(new PendingPaymentState());
    }

    @Override
    public void pay(Booking booking) {
        throw new IllegalStateException("Cannot pay: booking must be in PENDING_PAYMENT state first.");
    }

    @Override
    public void complete(Booking booking) {
        throw new IllegalStateException("Cannot complete: booking has not been paid yet.");
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.CONFIRMED;
    }
}
