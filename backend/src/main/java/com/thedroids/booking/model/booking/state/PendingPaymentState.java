package com.thedroids.booking.model.booking.state;

import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;

public class PendingPaymentState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        throw new IllegalStateException("Booking is already confirmed and awaiting payment.");
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
        throw new IllegalStateException("Booking is already in PENDING_PAYMENT state.");
    }

    @Override
    public void pay(Booking booking) {
        booking.setState(new PaidState());
    }

    @Override
    public void complete(Booking booking) {
        throw new IllegalStateException("Cannot complete: booking has not been paid yet.");
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.PENDING_PAYMENT;
    }
}
