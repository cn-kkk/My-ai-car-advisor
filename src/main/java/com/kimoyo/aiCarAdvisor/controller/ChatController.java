package com.kimoyo.aiCarAdvisor.controller;

import com.kimoyo.aiCarAdvisor.dto.ChatRequest;
import com.kimoyo.aiCarAdvisor.dto.ChatResponse;
import com.kimoyo.aiCarAdvisor.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatService chatService;
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        log.info("/chat请求: {}", request.toString());
        String convId = request.getConversationId();
        if (convId == null || convId.isBlank()) {
            convId = UUID.randomUUID().toString();
        }
        String answer = chatService.chat(convId, request.getMessage());
        String responseId = UUID.randomUUID().toString();
        ChatResponse chatResponse = new ChatResponse(responseId, convId, answer);
        log.info("/chat响应: {}", chatResponse.toString());
        return chatResponse;
    }
}