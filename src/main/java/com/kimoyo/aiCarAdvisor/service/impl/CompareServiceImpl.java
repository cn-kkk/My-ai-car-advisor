package com.kimoyo.aiCarAdvisor.service.impl;

import com.kimoyo.aiCarAdvisor.model.CarSpec;
import com.kimoyo.aiCarAdvisor.service.CarSpecService;
import com.kimoyo.aiCarAdvisor.service.CompareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CompareServiceImpl implements CompareService {

    private static final Logger log = LoggerFactory.getLogger(CompareServiceImpl.class);

    private final CarSpecService carSpecService;

    public CompareServiceImpl(CarSpecService carSpecService) {
        this.carSpecService = carSpecService;
    }

    @Override
    public String compare(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return "请重新输入具体的2款车型。";
        }
        List<String> parts = splitIntoTwo(userMessage);
        if (parts.size() < 2 || parts.get(0).isBlank() || parts.get(1).isBlank()) {
            return "请重新输入具体的2款车型。";
        }

        Optional<CarSpec> aOpt = carSpecService.findByMessage(parts.get(0));
        Optional<CarSpec> bOpt = carSpecService.findByMessage(parts.get(1));
        if (aOpt.isEmpty() || bOpt.isEmpty()) {
            return "请重新输入具体的2款车型。";
        }

        CarSpec A = aOpt.get();
        CarSpec B = bOpt.get();

        String titleA = A.getBrand() + " " + A.getSeries() + " " + A.getModel();
        String titleB = B.getBrand() + " " + B.getSeries() + " " + B.getModel();

        Map<String, String> attrA = A.getAttributes();
        Map<String, String> attrB = B.getAttributes();

        // 仅比较“支持状态”差异：
        // - 双方都不支持 => 跳过
        // - 一方支持、另一方不支持/无 => 列出
        // - 双方都支持 => 跳过
        Set<String> keys = new TreeSet<>();
        keys.addAll(attrA.keySet());
        keys.addAll(attrB.keySet());

        List<String[]> rows = new ArrayList<>();
        for (String k : keys) {
            String vA = attrA.getOrDefault(k, null);
            String vB = attrB.getOrDefault(k, null);
            boolean sA = isSupported(vA);
            boolean sB = isSupported(vB);
            if (!sA && !sB) {
                continue; // 双方都不支持，跳过
            }
            if (sA == sB) {
                // 双方都支持（或都不支持）则跳过；若之后需要对“支持但数值不同”的情况做扩展，可在这里处理
                continue;
            }
            rows.add(new String[]{k, prettyValue(vA, sA), prettyValue(vB, sB)});
        }

        StringBuilder sb = new StringBuilder();
        sb.append("已为你对比两款车型：\n");
        sb.append(titleA).append("  vs  ").append(titleB).append("\n\n");
        if (rows.isEmpty()) {
            sb.append("两款车型的支持项完全一致（仅按支持/不支持统计）。");
            return sb.toString();
        }

        sb.append("差异参数（仅展示一方支持、另一方不支持/无）：\n");
        sb.append("参数 | ").append(titleA).append(" | ").append(titleB).append("\n");
        sb.append("---- | ---- | ----\n");
        for (String[] r : rows) {
            sb.append(r[0]).append(" | ").append(r[1]).append(" | ").append(r[2]).append("\n");
        }
        sb.append("\n说明：本对比来自本地数据集，仅按支持状态统计；若需数值/具体配置差异，请告诉我你的偏好，我们可以扩展规则。");
        return sb.toString();
    }

    private static List<String> splitIntoTwo(String text) {
        String t = text == null ? "" : text.trim();
        // 优先按常见分隔词分割，命中后取前两个非空片段
        // 注意：允许分隔词两侧无空格（例如“model3和小米su7标准版”）
        String[] seps = new String[]{
                // 仅保留更可能出现在两款车型之间的分隔词，避免把动词“比较/对比”当分隔导致左片段如“帮我”
                "和", "与", "vs", "VS",
                "/", "|", ",", "，", "比一比", "pk", "PK"
        };
        for (String token : seps) {
            String sepTrim = token.trim();
            if (sepTrim.isEmpty()) continue;
            // 构造允许分隔词两侧可选空格的正则：\s*<sepTrim>\s*
            String regex = "\\s*" + java.util.regex.Pattern.quote(sepTrim) + "\\s*";
            if (t.toLowerCase().contains(sepTrim.toLowerCase())) {
                String[] arr = t.split(regex);
                java.util.List<String> res = new java.util.ArrayList<>();
                for (String a : arr) {
                    if (a != null && !a.trim().isBlank()) res.add(a.trim());
                }
                if (res.size() >= 2) return res.subList(0, 2);
            }
        }
        // 未命中显式分隔，尝试按空白分隔再回拼：
        // 假设用户输入为："品牌A 车系A 车型A 品牌B 车系B 车型B 对比"
        String[] ws = t.split("\\s+");
        if (ws.length >= 6) {
            // 简单地平均切半作为两个片段（启发式兜底）
            int mid = ws.length / 2;
            String left = String.join(" ", java.util.Arrays.copyOfRange(ws, 0, mid));
            String right = String.join(" ", java.util.Arrays.copyOfRange(ws, mid, ws.length));
            return java.util.Arrays.asList(left.trim(), right.trim());
        }
        return java.util.Collections.emptyList();
    }

    private static boolean isSupported(String v) {
        if (v == null) return false;
        String t = v.trim().toLowerCase();
        if (t.isBlank()) return false;
        // 典型不支持词
        String[] noWords = new String[]{"不支持", "无", "未提供", "否", "×", "-", "—", "n/a", "na"};
        for (String w : noWords) {
            if (t.contains(w)) return false;
        }
        // 典型支持/选装词
        String[] yesWords = new String[]{"标配", "支持", "可选", "选装", "提供", "是", "√", "有"};
        for (String w : yesWords) {
            if (t.contains(w)) return true;
        }
        // 若是数值/文本，默认视为“有配置/支持”
        return true;
    }

    private static String prettyValue(String v, boolean supported) {
        if (!supported) return "不支持/无";
        if (v == null || v.trim().isBlank()) return "不支持/无";
        return v.trim();
    }
}