package com.thedroids.booking.controller;

import com.thedroids.booking.service.BookingService;
import com.thedroids.booking.service.ConsultantService;
import com.thedroids.booking.model.booking.Booking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ConsultantService consultantService;

    public BookingController(BookingService bookingService, ConsultantService consultantService) {
        this.bookingService = bookingService;
        this.consultantService = consultantService;
    }

    @PostMapping
    public ResponseEntity<?> requestBooking(@RequestBody Map<String, String> body) {
        try {
            Booking booking = bookingService.requestBooking(
                    body.get("clientId"), body.get("consultantId"),
                    body.get("serviceId"), body.get("timeSlotId"));
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking cancelled."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/client/{clientId}")
    public List<Booking> clientBookings(@PathVariable String clientId) {
        return bookingService.getClientBookings(clientId);
    }

    @GetMapping("/consultant/{consultantId}")
    public List<Booking> consultantBookings(@PathVariable String consultantId) {
        return bookingService.getConsultantBookings(consultantId);
    }

    @GetMapping("/consultant/{consultantId}/pending")
    public List<Booking> pendingRequests(@PathVariable String consultantId) {
        return consultantService.getPendingRequests(consultantId);
    }

    @GetMapping("/consultant/{consultantId}/paid")
    public List<Booking> paidBookings(@PathVariable String consultantId) {
        return consultantService.getPaidBookings(consultantId);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(@PathVariable String id) {
        try {
            consultantService.acceptBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking accepted."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable String id) {
        try {
            consultantService.rejectBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking rejected."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable String id) {
        try {
            consultantService.completeBooking(id);
            return ResponseEntity.ok(Map.of("message", "Booking completed."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
