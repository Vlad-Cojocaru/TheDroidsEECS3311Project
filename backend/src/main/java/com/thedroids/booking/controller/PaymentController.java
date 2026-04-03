package com.thedroids.booking.controller;

import com.thedroids.booking.service.PaymentService;
import com.thedroids.booking.model.payment.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody Map<String, String> body) {
        try {
            Payment payment = paymentService.processPayment(
                    body.get("bookingId"), body.get("paymentMethodId"));
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history/{clientId}")
    public List<Payment> paymentHistory(@PathVariable String clientId) {
        return paymentService.getPaymentHistory(clientId);
    }
}
