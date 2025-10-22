package com.kimoyo.aiCarAdvisor;

import com.kimoyo.aiCarAdvisor.dto.ChatRequest;
import com.kimoyo.aiCarAdvisor.dto.ChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void chatEndpointReturnsAnswer() {
        ChatRequest req = new ChatRequest("请用一句话告诉我你是做什么的");
        ChatResponse resp = restTemplate.postForObject(
                "http://localhost:" + port + "/ai/chat",
                req,
                ChatResponse.class
        );

        assertThat(resp).isNotNull();
        System.out.println("ChatController answer: " + resp.getMessage());
        assertThat(resp.getMessage()).isNotBlank();
    }
}