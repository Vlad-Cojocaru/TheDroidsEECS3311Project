package com.thedroids.booking.controller;

import com.thedroids.booking.service.BookingService;
import com.thedroids.booking.model.service.ConsultingService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final BookingService bookingService;

    public ServiceController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<ConsultingService> browseServices() {
        return bookingService.browseServices();
    }
}
