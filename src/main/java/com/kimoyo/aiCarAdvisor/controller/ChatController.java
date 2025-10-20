package com.kimoyo.aiCarAdvisor.controller;

import com.kimoyo.aiCarAdvisor.dto.ChatRequest;
import com.kimoyo.aiCarAdvisor.dto.ChatResponse;
import com.kimoyo.aiCarAdvisor.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String answer = chatService.chat(request.getMessage());
        String responseId = UUID.randomUUID().toString();
        return new ChatResponse(responseId, request.getConversationId(), answer);
    }
}