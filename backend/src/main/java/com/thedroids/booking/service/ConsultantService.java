package com.thedroids.booking.service;

import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;
import com.thedroids.booking.model.notification.EventManager;
import com.thedroids.booking.model.notification.EventType;
import com.thedroids.booking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultantService {

    private final BookingRepository bookingRepository;
    private final EventManager eventManager;

    public ConsultantService(BookingRepository bookingRepository,
                             EventManager eventManager) {
        this.bookingRepository = bookingRepository;
        this.eventManager = eventManager;
    }

    public void acceptBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.confirm();
        booking.markPendingPayment();
        bookingRepository.save(booking);

        eventManager.notify(EventType.BOOKING_CONFIRMED,
                "Booking " + bookingId + " confirmed — awaiting payment.");
    }

    public void rejectBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.reject();
        bookingRepository.save(booking);

        eventManager.notify(EventType.BOOKING_REJECTED,
                "Booking " + bookingId + " has been rejected.");
    }

    public void completeBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.complete();
        bookingRepository.save(booking);

        eventManager.notify(EventType.BOOKING_COMPLETED,
                "Booking " + bookingId + " has been completed.");
    }

    public List<Booking> getPendingRequests(String consultantId) {
        return bookingRepository.findByConsultantId(consultantId).stream()
                .filter(b -> b.getStatus() == BookingStatus.REQUESTED)
                .toList();
    }

    public List<Booking> getPaidBookings(String consultantId) {
        return bookingRepository.findByConsultantId(consultantId).stream()
                .filter(b -> b.getStatus() == BookingStatus.PAID)
                .toList();
    }
}
