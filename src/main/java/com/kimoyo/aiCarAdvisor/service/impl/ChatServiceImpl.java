package com.kimoyo.aiCarAdvisor.service.impl;

import com.kimoyo.aiCarAdvisor.model.QwenModel;
import org.springframework.stereotype.Service;
import com.kimoyo.aiCarAdvisor.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

    private final QwenModel qwenModel;

    public ChatServiceImpl(QwenModel qwenModel) {
        this.qwenModel = qwenModel;
    }

    @Override
    public String chat(String userMessage) {
        return qwenModel.client()
                .prompt()
                .user(userMessage)
                .call()
                .content();
    }
}