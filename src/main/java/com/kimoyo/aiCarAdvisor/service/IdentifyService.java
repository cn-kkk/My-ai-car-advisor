package com.kimoyo.aiCarAdvisor.service;

public interface IdentifyService {
    /**
     * 调用百度车型识别，返回文本摘要消息。
     * @param imageBase64 纯Base64字符串（不含dataURL前缀）
     * @return 文本摘要，如“识别结果：XX（置信度xx%）”
     */
    String identify(String imageBase64);
}