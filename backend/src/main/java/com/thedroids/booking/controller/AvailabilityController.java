package com.thedroids.booking.controller;

import com.thedroids.booking.service.AvailabilityService;
import com.thedroids.booking.model.availability.TimeSlot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/{consultantId}")
    public List<TimeSlot> getAvailableSlots(@PathVariable String consultantId) {
        return availabilityService.getAvailableSlots(consultantId);
    }

    @GetMapping("/{consultantId}/all")
    public List<TimeSlot> getAllSlots(@PathVariable String consultantId) {
        return availabilityService.getAllSlots(consultantId);
    }

    @PostMapping
    public ResponseEntity<?> addTimeSlot(@RequestBody Map<String, String> body) {
        try {
            TimeSlot slot = availabilityService.addTimeSlot(
                    body.get("consultantId"),
                    LocalDate.parse(body.get("date")),
                    LocalTime.parse(body.get("startTime")),
                    LocalTime.parse(body.get("endTime")));
            return ResponseEntity.ok(slot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeTimeSlot(@PathVariable String id) {
        try {
            availabilityService.removeTimeSlot(id);
            return ResponseEntity.ok(Map.of("message", "Time slot removed."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
