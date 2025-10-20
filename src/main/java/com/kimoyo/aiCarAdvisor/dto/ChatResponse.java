package com.kimoyo.aiCarAdvisor.dto;

public class ChatResponse {
    private String responseId;
    private String conversationId;
    private String message;

    public ChatResponse() {
    }

    public ChatResponse(String message) {
        this.message = message;
    }

    public ChatResponse(String responseId, String conversationId, String message) {
        this.responseId = responseId;
        this.conversationId = conversationId;
        this.message = message;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}