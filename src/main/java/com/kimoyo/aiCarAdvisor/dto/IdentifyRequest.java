package com.kimoyo.aiCarAdvisor.dto;

public class IdentifyRequest {
    private String userId;
    private String requestId;
    private String conversationId;
    // 去掉dataURL前缀后的纯Base64字符串
    private String imageBase64;

    public IdentifyRequest() {}

    public IdentifyRequest(String userId, String requestId, String conversationId, String imageBase64) {
        this.userId = userId;
        this.requestId = requestId;
        this.conversationId = conversationId;
        this.imageBase64 = imageBase64;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    @Override
    public String toString() {
        return "IdentifyRequest{" +
                "userId='" + userId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", imageBase64Len=" + (imageBase64 == null ? 0 : imageBase64.length()) +
                '}';
    }
}