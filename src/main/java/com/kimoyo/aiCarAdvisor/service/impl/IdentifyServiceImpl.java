package com.kimoyo.aiCarAdvisor.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kimoyo.aiCarAdvisor.service.IdentifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class IdentifyServiceImpl implements IdentifyService {

    private static final Logger log = LoggerFactory.getLogger(IdentifyServiceImpl.class);

    @Value("${baidu.car.api-url}")
    private String apiUrl;

    @Value("${baidu.car.access-token}")
    private String accessToken;

    @Value("${baidu.car.timeout-ms:60000}")
    private int timeoutMs;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String identify(String imageBase64) {
        if (imageBase64 == null || imageBase64.isBlank()) {
            return "未提供图片数据";
        }
        try {
            // 构造URL（含access_token参数）
            String url = apiUrl + "?access_token=" + accessToken;

            // 注意：百度要求对Base64进行URLEncode
            String imgParam = URLEncoder.encode(imageBase64, StandardCharsets.UTF_8);
            StringBuilder sb = new StringBuilder();
            sb.append("image=").append(imgParam);
            // 业务逻辑：仅请求Top1，并返回百科信息（如有）
            sb.append("&top_num=").append(1);
            sb.append("&baike_num=").append(1);
            String body = sb.toString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(timeoutMs);
            factory.setReadTimeout(timeoutMs);

            RestTemplate restTemplate = new RestTemplate(factory);
            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
            String json = resp.getBody();
            log.info("百度识别响应: {}", json);

            if (json == null || json.isBlank()) {
                return "识别失败：空响应";
            }

            JsonNode root = mapper.readTree(json);
            // 解析结果数组，仅取Top1，并显示name/year与百科信息（如有）
            JsonNode resultArr = root.get("result");
            if (resultArr != null && resultArr.isArray() && resultArr.size() > 0) {
                JsonNode top1 = resultArr.get(0);
                String name = top1.path("name").asText("");
                String year = top1.path("year").asText("");

                StringBuilder summary = new StringBuilder();
                summary.append("识别结果：");
                if (!name.isBlank()) summary.append(name);
                if (!year.isBlank()) summary.append("（年份：").append(year).append("）");

                // 百科信息（若返回）
                JsonNode baike = top1.path("baike_info");
                String baikeUrl = baike.path("baike_url").asText("");
                String baikeDesc = baike.path("description").asText("");
                if (!baikeUrl.isBlank()) {
                    summary.append("\n百科链接：").append(baikeUrl);
                }
                if (!baikeDesc.isBlank()) {
                    summary.append("\n百科简介：").append(baikeDesc);
                }
                return summary.toString();
            }

            // 如有error_msg等
            String errMsg = root.path("error_msg").asText("");
            if (!errMsg.isBlank()) {
                return "识别失败：" + errMsg;
            }
            return "识别失败：未返回结果";
        } catch (Exception e) {
            log.error("调用百度车型识别异常", e);
            return "识别失败：服务异常，请稍后重试";
        }
    }
}