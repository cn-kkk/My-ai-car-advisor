package com.kimoyo.aiCarAdvisor.service.impl;

import com.kimoyo.aiCarAdvisor.service.ChatService;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kimoyo.aiCarAdvisor.service.CarSpecService;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatModel chatModel;
    private final String systemPrompt;
    private final Map<String, Deque<Message>> memory = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final CarSpecService carSpecService;

    public ChatServiceImpl(ChatModel chatModel,
                           CarSpecService carSpecService,
                           @Value("classpath:/prompts/car-advisor-system.txt") String systemPrompt) {
        this.chatModel = chatModel;
        this.carSpecService = carSpecService;
        this.systemPrompt = systemPrompt;
    }

    @Override
    public String chat(String conversationId, String userMessage) {
        String key = (conversationId == null || conversationId.isBlank()) ? "default" : conversationId;
        Deque<Message> history = memory.computeIfAbsent(key, k -> new ArrayDeque<>());

        if (history.isEmpty()) {
            history.add(new SystemMessage(systemPrompt));
        }
        history.add(new UserMessage(userMessage));

        // 先尝试走本地CSV结构化查询（不区分年款，默认最新），且仅在“参数/配置”意图下触发
        try {
            if (carSpecService.isSpecQuery(userMessage)) {
                var hit = carSpecService.findByMessage(userMessage);
                if (hit.isPresent()) {
                    String answer = carSpecService.format(hit.get());
                    history.add(new AssistantMessage(answer));
                    log.info("命中本地CSV数据集（参数意图），直接返回结构化结果");
                    trimWindow(history, 40);
                    return answer;
                }
            }
        } catch (Exception e) {
            log.warn("本地CSV查询失败，回退到LLM：{}", e.toString());
        }

        trimWindow(history, 40);

        log.info("准备请求qwen chatModel");
        long startTime = System.currentTimeMillis();
        ChatResponse resp = chatModel.call(new Prompt(new ArrayList<>(history)));
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        String answer = resp.getResults().get(0).getOutput().getText();
        log.info("qwen chatModel耗时: {}ms", duration);
        log.info("qwen chatModel响应: {}", answer);
        history.add(new AssistantMessage(answer));
        return answer;
    }

    private void trimWindow(Deque<Message> q, int max) {
        while (q.size() > max) q.pollFirst();
    }
}