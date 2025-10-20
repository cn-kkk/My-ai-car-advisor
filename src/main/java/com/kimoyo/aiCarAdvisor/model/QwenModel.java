package com.kimoyo.aiCarAdvisor.model;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QwenModel {

    private final ChatClient chatClient;

    public QwenModel(ChatModel chatModel,
                     @Value("classpath:/prompts/car-advisor-system.txt") String systemPrompt) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .build();
    }

    public ChatClient client() {
        return chatClient;
    }
}