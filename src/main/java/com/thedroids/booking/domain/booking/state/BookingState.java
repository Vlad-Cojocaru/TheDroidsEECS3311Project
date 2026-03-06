package com.thedroids.booking.domain.booking.state;

import com.thedroids.booking.domain.booking.Booking;
import com.thedroids.booking.domain.booking.BookingStatus;

/**
 * State pattern interface for the booking lifecycle.
 * Each concrete state defines which transitions are valid
 * and throws IllegalStateException for invalid ones.
 */
public interface BookingState {

    void confirm(Booking booking);

    void reject(Booking booking);

    void cancel(Booking booking);

    void markPendingPayment(Booking booking);

    void pay(Booking booking);

    void complete(Booking booking);

    BookingStatus getStatus();
}
