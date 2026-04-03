package com.thedroids.booking.controller;

import com.thedroids.booking.service.PaymentService;
import com.thedroids.booking.model.payment.PaymentMethod;
import com.thedroids.booking.model.payment.PaymentMethodType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    private final PaymentService paymentService;

    public PaymentMethodController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{clientId}")
    public List<PaymentMethod> getPaymentMethods(@PathVariable String clientId) {
        return paymentService.getPaymentMethods(clientId);
    }

    @PostMapping
    public ResponseEntity<?> addPaymentMethod(@RequestBody Map<String, Object> body) {
        try {
            String clientId = (String) body.get("clientId");
            PaymentMethodType type = PaymentMethodType.valueOf(((String) body.get("type")).toUpperCase());
            @SuppressWarnings("unchecked")
            Map<String, String> details = (Map<String, String>) body.get("details");
            PaymentMethod method = paymentService.addPaymentMethod(clientId, type, details);
            return ResponseEntity.ok(method);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePaymentMethod(@PathVariable String id) {
        try {
            paymentService.removePaymentMethod(id);
            return ResponseEntity.ok(Map.of("message", "Payment method removed."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
