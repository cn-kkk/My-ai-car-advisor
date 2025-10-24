package com.kimoyo.aiCarAdvisor.controller;

import com.kimoyo.aiCarAdvisor.dto.IdentifyRequest;
import com.kimoyo.aiCarAdvisor.dto.IdentifyResponse;
import com.kimoyo.aiCarAdvisor.service.IdentifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class IdentifyController {

    private final IdentifyService identifyService;
    private static final Logger log = LoggerFactory.getLogger(IdentifyController.class);

    public IdentifyController(IdentifyService identifyService) {
        this.identifyService = identifyService;
    }

    @PostMapping("/identify")
    public IdentifyResponse identify(@RequestBody IdentifyRequest request) {
        log.info("/identify请求: {}", request.toString());
        String convId = request.getConversationId();
        if (convId == null || convId.isBlank()) {
            convId = UUID.randomUUID().toString();
        }
        String base64 = request.getImageBase64();
        // 后端兜底校验4MB限制（Base64长度近似换算为原大小：len * 3/4）
        int approxBytes = base64 == null ? 0 : (base64.length() * 3) / 4;
        final int MAX_BYTES = 4 * 1024 * 1024; // 4MB
        String msg;
        if (approxBytes > MAX_BYTES) {
            msg = "图片超过4MB限制，无法识别，请更换更小的图片。";
        } else {
            msg = identifyService.identify(base64);
        }
        String responseId = UUID.randomUUID().toString();
        IdentifyResponse resp = new IdentifyResponse(responseId, convId, msg);
        log.info("/identify响应: {}", resp.toString());
        return resp;
    }
}