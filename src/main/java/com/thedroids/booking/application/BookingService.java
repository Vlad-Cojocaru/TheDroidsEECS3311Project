package com.thedroids.booking.application;

import com.thedroids.booking.domain.availability.TimeSlot;
import com.thedroids.booking.domain.booking.Booking;
import com.thedroids.booking.domain.booking.BookingStatus;
import com.thedroids.booking.domain.service.ConsultingService;
import com.thedroids.booking.ports.repository.BookingRepository;
import com.thedroids.booking.ports.repository.ServiceRepository;
import com.thedroids.booking.ports.repository.TimeSlotRepository;

import com.thedroids.booking.domain.notification.EventManager;

import java.util.List;
import java.util.UUID;

/**
 * Handles UC1 (Browse Services), UC2 (Request Booking),
 * UC3 (Cancel Booking), UC4 (View Booking History).
 */
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final EventManager eventManager;

    public BookingService(BookingRepository bookingRepository,
                          ServiceRepository serviceRepository,
                          TimeSlotRepository timeSlotRepository,
                          EventManager eventManager) {
        this.bookingRepository = bookingRepository;
        this.serviceRepository = serviceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.eventManager = eventManager;
    }

    // UC1: Browse all available consulting services. 
    public List<ConsultingService> browseServices() {
        return serviceRepository.findAll();
    }

    // UC2: Client requests a booking for a specific service and time slot. 
    public Booking requestBooking(String clientId, String consultantId,
                                  String serviceId, String timeSlotId) {
        ConsultingService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));

        TimeSlot slot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new IllegalArgumentException("Time slot not found: " + timeSlotId));

        if (!slot.isAvailable()) {
            throw new IllegalStateException("Time slot is no longer available.");
        }

        slot.setAvailable(false);
        timeSlotRepository.save(slot);

        Booking booking = new Booking(
                UUID.randomUUID().toString(),
                clientId, consultantId, serviceId, timeSlotId);
        bookingRepository.save(booking);

        return booking;
    }

    // UC3: Client cancels an existing booking.
    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));

        booking.cancel();
        bookingRepository.save(booking);

        TimeSlot slot = timeSlotRepository.findById(booking.getTimeSlotId()).orElse(null);
        if (slot != null) {
            slot.setAvailable(true);
            timeSlotRepository.save(slot);
        }
    }

    // UC4 View all bookings for a specific client
    public List<Booking> getClientBookings(String clientId) {
        return bookingRepository.findByClientId(clientId);
    }

    // Get bookings awaiting a consultant's action
    public List<Booking> getConsultantBookings(String consultantId) {
        return bookingRepository.findByConsultantId(consultantId);
    }

    // Get bookings in REQUESTED state for a consultant
    public List<Booking> getPendingRequests(String consultantId) {
        return bookingRepository.findByConsultantId(consultantId).stream()
                .filter(b -> b.getStatus() == BookingStatus.REQUESTED)
                .toList();
    }

    public Booking getBooking(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }
}
