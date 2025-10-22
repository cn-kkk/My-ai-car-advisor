package com.kimoyo.aiCarAdvisor.service;

public interface CompareService {
    /**
     * 根据用户输入的文本，解析两款车型并返回差异对比的表格文本。
     * 若无法解析出两款车型，返回提示语："请重新输入具体的2款车型。"。
     */
    String compare(String userMessage);
}