package com.thedroids.booking.application;

import com.thedroids.booking.domain.booking.Booking;
import com.thedroids.booking.domain.booking.BookingStatus;
import com.thedroids.booking.domain.notification.EventManager;
import com.thedroids.booking.domain.notification.EventType;
import com.thedroids.booking.ports.repository.BookingRepository;

import java.util.List;

/**
 * Handles UC9 (Accept/Reject Booking) and UC10 (Complete Booking).
 */
public class ConsultantService {

    private final BookingRepository bookingRepository;
    private final EventManager eventManager;

    public ConsultantService(BookingRepository bookingRepository,
                             EventManager eventManager) {
        this.bookingRepository = bookingRepository;
        this.eventManager = eventManager;
    }

    /** UC9 — Consultant accepts a booking request. Moves to Confirmed then PendingPayment. */
    public void acceptBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.confirm();
        booking.markPendingPayment();
        bookingRepository.save(booking);

        eventManager.notify(EventType.BOOKING_CONFIRMED,
                "Booking " + bookingId + " confirmed — awaiting payment.");
    }

    /** UC9 — Consultant rejects a booking request. */
    public void rejectBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.reject();
        bookingRepository.save(booking);

        eventManager.notify(EventType.BOOKING_REJECTED,
                "Booking " + bookingId + " has been rejected.");
    }

    /** UC10 — Consultant marks a paid booking as completed. */
    public void completeBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.complete();
        bookingRepository.save(booking);

        eventManager.notify(EventType.BOOKING_COMPLETED,
                "Booking " + bookingId + " has been completed.");
    }

    /** Get all bookings in REQUESTED state for a consultant. */
    public List<Booking> getPendingRequests(String consultantId) {
        return bookingRepository.findByConsultantId(consultantId).stream()
                .filter(b -> b.getStatus() == BookingStatus.REQUESTED)
                .toList();
    }

    /** Get all bookings in PAID state for a consultant . */
    public List<Booking> getPaidBookings(String consultantId) {
        return bookingRepository.findByConsultantId(consultantId).stream()
                .filter(b -> b.getStatus() == BookingStatus.PAID)
                .toList();
    }
}
