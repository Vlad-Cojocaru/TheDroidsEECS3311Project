package com.thedroids.booking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedroids.booking.model.service.ConsultingService;
import com.thedroids.booking.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String model;

    private final ServiceRepository serviceRepository;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public String ask(String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            return "[Fallback mode — no API key configured]\n\n" + getFallbackResponse(userMessage);
        }

        try {
            String systemPrompt = buildSystemPrompt();
            String requestBody = buildRequestBody(systemPrompt, userMessage);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String errorDetail = extractApiError(response.body());
                System.err.println("[ChatService] OpenAI returned HTTP " + response.statusCode() + ": " + errorDetail);
                return "[API error " + response.statusCode() + ": " + errorDetail + "]\n\n"
                        + getFallbackResponse(userMessage);
            }

            return extractContent(response.body());
        } catch (Exception e) {
            System.err.println("[ChatService] Exception: " + e.getMessage());
            return "[Connection error: " + e.getClass().getSimpleName() + " — " + e.getMessage() + "]\n\n"
                    + getFallbackResponse(userMessage);
        }
    }

    private String buildSystemPrompt() {
        List<ConsultingService> services = serviceRepository.findAll();
        String serviceList = services.stream()
                .map(s -> "- " + s.getName() + " (" + s.getType() + "): " + s.getDescription()
                        + " | " + s.getDurationMinutes() + " min | $" + s.getBasePrice())
                .collect(Collectors.joining("\n"));

        return "You are the customer assistant for The Droids Service Booking & Consulting Platform. "
                + "Help users with questions about the platform, booking process, payment methods, and policies.\n\n"
                + "PLATFORM INFORMATION:\n"
                + "- Users can browse consulting services, request bookings, and pay for sessions.\n"
                + "- Booking flow: Client requests -> Consultant accepts/rejects -> Client pays -> Session completed.\n"
                + "- Payment methods: Credit Card, Debit Card, PayPal, Bank Transfer (all simulated).\n"
                + "- Cancellation policy: Bookings can be cancelled before completion. Default window is 24 hours.\n"
                + "- Refund policy: 90% refund on cancellations within the window.\n\n"
                + "AVAILABLE SERVICES:\n" + (serviceList.isEmpty() ? "No services currently listed." : serviceList)
                + "\n\nIMPORTANT: Never share personal user data, payment details, or private booking information. "
                + "Only provide general platform information. Keep responses concise and helpful.";
    }

    private String buildRequestBody(String systemPrompt, String userMessage) {
        try {
            var messages = objectMapper.createArrayNode();
            messages.add(objectMapper.createObjectNode()
                    .put("role", "system").put("content", systemPrompt));
            messages.add(objectMapper.createObjectNode()
                    .put("role", "user").put("content", userMessage));

            var root = objectMapper.createObjectNode();
            root.put("model", model);
            root.set("messages", messages);
            root.put("max_tokens", 500);
            root.put("temperature", 0.7);
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build request JSON", e);
        }
    }

    private String extractContent(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.isNull()) {
                return "Sorry, the AI returned an empty response.";
            }
            return content.asText();
        } catch (Exception e) {
            System.err.println("[ChatService] Failed to parse response: " + e.getMessage());
            return "Sorry, I could not parse the AI response.";
        }
    }

    private String extractApiError(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode msg = root.path("error").path("message");
            if (!msg.isMissingNode()) return msg.asText();
        } catch (Exception ignored) {}
        return "unknown error";
    }

    private String getFallbackResponse(String userMessage) {
        String lower = userMessage.toLowerCase();
        if (lower.contains("book")) {
            return "To book a session: 1) Browse available services, 2) Select a consultant and time slot, "
                    + "3) Submit a booking request, 4) Wait for consultant to accept, 5) Pay for the session.";
        } else if (lower.contains("pay")) {
            return "We accept Credit Card, Debit Card, PayPal, and Bank Transfer. "
                    + "Add a payment method in your dashboard, then pay for confirmed bookings.";
        } else if (lower.contains("cancel")) {
            return "You can cancel a booking before it's completed. The default cancellation window is 24 hours. "
                    + "Cancellations within the window receive a 90% refund.";
        } else if (lower.contains("service")) {
            List<ConsultingService> services = serviceRepository.findAll();
            if (services.isEmpty()) return "No services are currently available. Please check back later.";
            return "Available services: " + services.stream()
                    .map(s -> s.getName() + " ($" + s.getBasePrice() + ")")
                    .collect(Collectors.joining(", "));
        }
        return "I can help with questions about booking sessions, payment methods, cancellation policies, "
                + "and available services. What would you like to know?";
    }
}
