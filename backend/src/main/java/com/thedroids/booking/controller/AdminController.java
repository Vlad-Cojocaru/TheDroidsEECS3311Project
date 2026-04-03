package com.thedroids.booking.controller;

import com.thedroids.booking.service.AdminService;
import com.thedroids.booking.model.policy.SystemPolicy;
import com.thedroids.booking.model.user.Consultant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/pending-consultants")
    public List<Consultant> pendingConsultants() {
        return adminService.getPendingConsultants();
    }

    @PutMapping("/consultants/{id}/approve")
    public ResponseEntity<?> approveConsultant(@PathVariable String id) {
        try {
            adminService.approveConsultant(id);
            return ResponseEntity.ok(Map.of("message", "Consultant approved."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/consultants/{id}/reject")
    public ResponseEntity<?> rejectConsultant(@PathVariable String id) {
        try {
            adminService.rejectConsultant(id);
            return ResponseEntity.ok(Map.of("message", "Consultant rejected."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/policy")
    public SystemPolicy getPolicy() {
        return adminService.getCurrentPolicy();
    }

    @PutMapping("/policy")
    public ResponseEntity<?> updatePolicy(@RequestBody SystemPolicy policy) {
        try {
            adminService.updatePolicy(policy);
            return ResponseEntity.ok(Map.of("message", "Policy updated."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
