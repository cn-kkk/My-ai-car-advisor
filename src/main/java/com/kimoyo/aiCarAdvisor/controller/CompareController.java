package com.kimoyo.aiCarAdvisor.controller;

import com.kimoyo.aiCarAdvisor.dto.CompareRequest;
import com.kimoyo.aiCarAdvisor.dto.CompareResponse;
import com.kimoyo.aiCarAdvisor.service.CompareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class CompareController {

    private final CompareService compareService;
    private static final Logger log = LoggerFactory.getLogger(CompareController.class);

    public CompareController(CompareService compareService) {
        this.compareService = compareService;
    }

    @PostMapping("/compare")
    public CompareResponse compare(@RequestBody CompareRequest request) {
        log.info("/compare请求: {}", request.toString());
        String convId = request.getConversationId();
        if (convId == null || convId.isBlank()) {
            convId = UUID.randomUUID().toString();
        }
        String result = compareService.compare(request.getMessage());
        String responseId = UUID.randomUUID().toString();
        CompareResponse resp = new CompareResponse(responseId, convId, result);
        log.info("/compare响应: {}", resp.toString());
        return resp;
    }
}