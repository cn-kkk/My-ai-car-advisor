package com.kimoyo.aiCarAdvisor.dto;

public class IdentifyResponse {
    private String responseId;
    private String conversationId;
    private String message; // 返回文本摘要，便于前端直接显示

    public IdentifyResponse() {}

    public IdentifyResponse(String responseId, String conversationId, String message) {
        this.responseId = responseId;
        this.conversationId = conversationId;
        this.message = message;
    }

    public String getResponseId() { return responseId; }
    public void setResponseId(String responseId) { this.responseId = responseId; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "IdentifyResponse{" +
                "responseId='" + responseId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}