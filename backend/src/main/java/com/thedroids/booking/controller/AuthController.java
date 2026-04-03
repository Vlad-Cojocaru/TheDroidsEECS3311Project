package com.thedroids.booking.controller;

import com.thedroids.booking.model.user.*;
import com.thedroids.booking.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password."));
        }

        if (user instanceof Consultant c && !c.isApproved()) {
            return ResponseEntity.status(403).body(Map.of("error", "Account pending admin approval."));
        }

        return ResponseEntity.ok(buildUserResponse(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String role = body.getOrDefault("role", "CLIENT").toUpperCase();

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered."));
        }

        String id = UUID.randomUUID().toString();
        User user;
        if ("CONSULTANT".equals(role)) {
            String specialization = body.getOrDefault("specialization", "General");
            double hourlyRate = Double.parseDouble(body.getOrDefault("hourlyRate", "100.0"));
            user = new Consultant(id, name, email, password, specialization, hourlyRate);
        } else {
            user = new Client(id, name, email, password);
        }

        userRepository.save(user);
        return ResponseEntity.ok(buildUserResponse(user));
    }

    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("id", user.getId());
        resp.put("name", user.getName());
        resp.put("email", user.getEmail());
        resp.put("role", user.getRole());
        if (user instanceof Consultant c) {
            resp.put("approved", c.isApproved());
            resp.put("specialization", c.getSpecialization());
            resp.put("hourlyRate", c.getHourlyRate());
        }
        return resp;
    }
}
