package com.thedroids.booking.controller;

import com.thedroids.booking.service.ChatService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "");
        String response = chatService.ask(message);
        return Map.of("response", response);
    }
}
