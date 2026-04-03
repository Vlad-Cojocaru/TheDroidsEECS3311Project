package com.thedroids.booking.service;

import com.thedroids.booking.model.availability.TimeSlot;
import com.thedroids.booking.model.booking.Booking;
import com.thedroids.booking.model.booking.BookingStatus;
import com.thedroids.booking.model.service.ConsultingService;
import com.thedroids.booking.model.notification.EventManager;
import com.thedroids.booking.repository.BookingRepository;
import com.thedroids.booking.repository.ServiceRepository;
import com.thedroids.booking.repository.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
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

    public List<ConsultingService> browseServices() {
        return serviceRepository.findAll();
    }

    public Booking requestBooking(String clientId, String consultantId,
                                  String serviceId, String timeSlotId) {
        serviceRepository.findById(serviceId)
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

    public List<Booking> getClientBookings(String clientId) {
        return bookingRepository.findByClientId(clientId);
    }

    public List<Booking> getConsultantBookings(String consultantId) {
        return bookingRepository.findByConsultantId(consultantId);
    }

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
