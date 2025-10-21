package com.kimoyo.aiCarAdvisor.dto;

public class ChatRequest {
    private String userId;
    private String requestId;
    private String conversationId;
    private String message;

    public ChatRequest() {
    }

    public ChatRequest(String message) {
        this.message = message;
    }

    public ChatRequest(String userId, String requestId, String conversationId, String message) {
        this.userId = userId;
        this.requestId = requestId;
        this.conversationId = conversationId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
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

    @Override
    public String toString() {
        return "ChatRequest{" +
                "userId='" + userId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}