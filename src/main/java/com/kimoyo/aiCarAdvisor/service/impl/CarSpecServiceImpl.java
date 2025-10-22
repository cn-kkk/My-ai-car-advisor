package com.kimoyo.aiCarAdvisor.service.impl;

import com.kimoyo.aiCarAdvisor.model.CarSpec;
import com.kimoyo.aiCarAdvisor.service.CarSpecService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CarSpecServiceImpl implements CarSpecService {

    private static final Logger log = LoggerFactory.getLogger(CarSpecServiceImpl.class);

    private final Map<String, CarSpec> byKey = new ConcurrentHashMap<>();
    private final Set<String> brandDict = ConcurrentHashSet();
    private final Set<String> seriesDict = ConcurrentHashSet();
    private final Set<String> modelDict = ConcurrentHashSet();

    @PostConstruct
    public void init() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:data/*.csv");
            if (resources.length == 0) {
                log.warn("未找到任何 data/*.csv，本地参数查询将不可用");
            }
            for (Resource res : resources) {
                loadCsv(res);
            }
            log.info("CSV车型参数加载完成，共计 {} 条", byKey.size());
        } catch (Exception e) {
            log.error("加载CSV失败", e);
        }
    }

    private void loadCsv(Resource resource) {
        int addedCount = 0;
        String fileName = resource.getFilename();
        String nameNoExt = stripExt(fileName);
        String brandFromName = null;
        String seriesFromName = null;
        if (nameNoExt != null && nameNoExt.contains("_")) {
            String[] parts = nameNoExt.split("_", 2);
            brandFromName = parts[0];
            seriesFromName = parts[1];
        } else {
            // 文件名无法分出品牌与车系时，全部当作车系
            seriesFromName = nameNoExt;
        }
    
        String csvText;
        try (InputStream is = resource.getInputStream()) {
            csvText = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("读取CSV失败: {}", fileName, e);
            return;
        }
    
        boolean parsed = false;
        for (char delimiter : new char[]{',', ';', '\t', '|', '，'}) {
            CSVFormat fmt = CSVFormat.DEFAULT.builder()
                    .setDelimiter(delimiter)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .setQuote('"')
                    .setAllowMissingColumnNames(true)
                    .setSkipHeaderRecord(false)
                    .build()
                    .withFirstRecordAsHeader();
            try (CSVParser parser = fmt.parse(new StringReader(csvText))) {
                Map<String, Integer> header = parser.getHeaderMap();
                if (header == null || header.isEmpty()) {
                    continue; // 尝试下一个分隔符
                }
                // 矩阵型（首列为参数项，后续列为不同ID/型号）
                if (isMatrixHeader(header)) {
                    int c = parseMatrixCsv(header, parser.getRecords(), brandFromName, seriesFromName);
                    addedCount += c;
                    parsed = true;
                } else {
                    // 行即车型记录的常规CSV
                    for (CSVRecord row : parser) {
                        String brand = firstNonBlank(row, header, "brand", "品牌", "厂商", "品牌名称");
                        String series = firstNonBlank(row, header, "series", "车系", "车系名称", "系列");
                        String model = firstNonBlank(row, header, "model", "车型", "款型", "车型名称", "型号", "车款", "配置", "版本", "配置名称", "车型配置");
    
                        if (isBlank(brand)) brand = brandFromName;
                        if (isBlank(series)) series = seriesFromName;
    
                        if (isBlank(model)) {
                            continue;
                        }
                        if (isBlank(brand) || isBlank(series)) {
                            continue;
                        }
    
                        CarSpec spec = new CarSpec(brand.trim(), series.trim(), model.trim());
                        for (String col : header.keySet()) {
                            if (equalsAnyIgnoreCase(col, "brand", "品牌", "厂商", "品牌名称",
                                    "series", "车系", "车系名称", "系列",
                                    "model", "车型", "款型", "车型名称", "型号", "车款")) {
                                continue;
                            }
                            String v = sanitizeValue(row.get(col));
                            if (!isBlank(v)) {
                                spec.putAttribute(col, v);
                            }
                        }
                        String key = keyOf(brand, series, model);
                        byKey.put(key, spec);
                        brandDict.add(normalize(brand));
                        seriesDict.add(normalize(series));
                        modelDict.add(normalize(model));
                        addedCount++;
                    }
                    parsed = true;
                }
                break; // 已成功解析并处理
            } catch (Exception ex) {
                // 尝试下一个分隔符
            }
        }
    
        if (!parsed) {
            log.warn("无法解析CSV: {}", fileName);
        } else {
            log.info("加载CSV: {}，新增记录: {}", fileName, addedCount);
        }
    }

    private static String stripExt(String name) {
        if (name == null) return null;
        int i = name.lastIndexOf('.');
        return i > 0 ? name.substring(0, i) : name;
    }

    @Override
    public Optional<CarSpec> findByMessage(String userText) {
        if (isBlank(userText)) return Optional.empty();
        String text = normalize(userText);
        // 近似命中集（编辑距离<=1），用于容错一个字差错
        Set<String> approxBrands = approxContainsAny(text, brandDict, 1);
        Set<String> approxSeries = approxContainsAny(text, seriesDict, 1);
        Set<String> approxModels = approxContainsAny(text, modelDict, 1);

        // 先尝试精确组合匹配（brand+series+model）
        List<Hit> hits = new ArrayList<>();
        for (CarSpec spec : byKey.values()) {
            String nBrand = normalize(spec.getBrand());
            String nSeries = normalize(spec.getSeries());
            String nModel = normalize(spec.getModel());

            boolean hasModel = text.contains(nModel) || approxModels.contains(nModel);
            boolean hasBrand = text.contains(nBrand) || approxBrands.contains(nBrand);
            boolean hasSeries = text.contains(nSeries) || approxSeries.contains(nSeries);

            if (hasModel && (hasBrand || hasSeries)) {
                hits.add(new Hit(spec, nModel.length() + (hasBrand ? nBrand.length() : 0) + (hasSeries ? nSeries.length() : 0)));
            } else if (hasModel) {
                // 只有车型也可命中，但可能多义，后面用最长模型名消歧
                hits.add(new Hit(spec, nModel.length()));
            }
        }
        // 精确匹配命中
        if (!hits.isEmpty()) {
            hits.sort((a, b) -> Integer.compare(b.score, a.score));
            return Optional.of(hits.get(0).spec);
        }

        // 品牌+车系兜底：当文本同时包含品牌与车系，但未包含具体型号时，
        // 若该品牌+车系仅有一个型号，直接返回；若存在多个型号，自动选择“参数最丰富”的型号作为默认。
        List<CarSpec> candidates = new ArrayList<>();
        for (CarSpec spec : byKey.values()) {
            String nBrand = normalize(spec.getBrand());
            String nSeries = normalize(spec.getSeries());
            boolean hasBrand = text.contains(nBrand) || approxBrands.contains(nBrand);
            boolean hasSeries = text.contains(nSeries) || approxSeries.contains(nSeries);
            if (hasBrand && hasSeries) {
                candidates.add(spec);
            }
        }
        if (candidates.size() == 1) {
            return Optional.of(candidates.get(0));
        } else if (candidates.size() > 1) {
            CarSpec best = null;
            int bestCount = -1;
            for (CarSpec s : candidates) {
                int c = (s.getAttributes() == null) ? 0 : s.getAttributes().size();
                if (c > bestCount) {
                    best = s;
                    bestCount = c;
                }
            }
            if (best != null) return Optional.of(best);
        }

        // 仅车系兜底：文本包含车系，但没有明确车型和品牌时也尝试匹配
        List<CarSpec> seriesOnly = new ArrayList<>();
        for (CarSpec spec : byKey.values()) {
            String nSeries = normalize(spec.getSeries());
            String nModel = normalize(spec.getModel());
            String nBrand = normalize(spec.getBrand());
            boolean hasSeries = text.contains(nSeries) || approxSeries.contains(nSeries);
            boolean hasModel = text.contains(nModel) || approxModels.contains(nModel);
            // 如果已包含车型，前面的精确逻辑应当命中；这里只处理“不含车型”的系列查询
            if (hasSeries && !hasModel) {
                seriesOnly.add(spec);
            }
        }
        if (seriesOnly.size() == 1) {
            return Optional.of(seriesOnly.get(0));
        } else if (seriesOnly.size() > 1) {
            CarSpec best = null;
            int bestCount = -1;
            for (CarSpec s : seriesOnly) {
                int c = (s.getAttributes() == null) ? 0 : s.getAttributes().size();
                if (c > bestCount) {
                    best = s;
                    bestCount = c;
                }
            }
            if (best != null) return Optional.of(best);
        }

        return Optional.empty();
    }

    @Override
    public String format(CarSpec spec) {
        StringBuilder sb = new StringBuilder();
        sb.append("已基于本地数据集为你查询到车型参数：\n");
        sb.append("品牌：").append(spec.getBrand()).append("；");
        sb.append("车系：").append(spec.getSeries()).append("；");
        sb.append("车型：").append(spec.getModel()).append("\n");
        if (!spec.getAttributes().isEmpty()) {
            sb.append("主要参数：\n");
            for (Map.Entry<String, String> e : spec.getAttributes().entrySet()) {
                sb.append(" - ").append(e.getKey()).append("：").append(e.getValue()).append("\n");
            }
        }
        sb.append("\n说明：本结果来自本地数据集，2025年8月更新。");
        return sb.toString();
    }

    @Override
    public boolean isSpecQuery(String userText) {
        if (userText == null) return false;
        String t = userText.trim().toLowerCase();
        String[] mustAny = new String[]{
                "参数", "配置", "详细参数", "数据", "规格", "性能",
                "油耗", "百公里", "马力", "功率", "扭矩", "尺寸", "轴距", "电池", "续航",
                "查看", "查一下", "给我", "是多少", "多少"
        };
        for (String k : mustAny) {
            if (t.contains(k)) return true;
        }
        // 如果是非常短的问句且仅有车型名，也可能希望看到参数
        // 例如："宋PLUS 参数"，上面也已覆盖
        return false;
    }

    // --- helpers ---

    private static String keyOf(String brand, String series, String model) {
        return normalize(brand) + "|" + normalize(series) + "|" + normalize(model);
    }

    private static String firstNonBlank(CSVRecord row, Map<String, Integer> header, String... names) {
        for (String n : names) {
            // 先尝试直接不区分大小写按列名取值
            for (String h : header.keySet()) {
                if (canonicalizeHeader(h).equals(canonicalizeHeader(n)) || h.equalsIgnoreCase(n)) {
                    String v = row.get(h);
                    if (!isBlank(v)) return v;
                }
            }
        }
        return null;
    }

    private static String canonicalizeHeader(String s) {
        if (s == null) return "";
        String t = s.trim().toLowerCase(Locale.ROOT);
        // 去除BOM与空格，提升匹配鲁棒性
        t = t.replace("\uFEFF", "");
        t = t.replace(" ", "");
        return t;
    }

    private static String sanitizeValue(String v) {
        if (v == null) return null;
        String t = v;
        // HTML 实体转为普通空格
        t = t.replace("&nbsp;", " ");
        t = t.replace("&ensp;", " ");
        t = t.replace("&emsp;", " ");
        t = t.replace("&thinsp;", " ");
        // 不间断空格与零宽字符
        t = t.replace("\u00A0", " "); // NBSP
        t = t.replace("\u200B", ""); // ZERO WIDTH SPACE
        t = t.replace("\u200C", ""); // ZERO WIDTH NON-JOINER
        t = t.replace("\u200D", ""); // ZERO WIDTH JOINER
        t = t.replace("\uFEFF", ""); // BOM
        // 统一修剪
        t = t.trim();
        return t;
    }

    private static boolean equalsAnyIgnoreCase(String s, String... arr) {
        for (String a : arr) {
            if (s.equalsIgnoreCase(a)) return true;
        }
        return false;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String t = s.trim().toLowerCase(Locale.ROOT);
        // 去除常见分隔符和空白，提高匹配鲁棒性
        t = t.replace("·", "");
        t = t.replace("+", "");
        t = t.replace("-", "");
        t = t.replace(" ", "");
        t = t.replace("_", "");
        // 额外处理全角空格
        t = t.replace("\u3000", "");
        // 处理不间断空格
        t = t.replace("\u00A0", "");
        return t;
    }

    // 近似包含：允许一个字符的替换/插入/删除（编辑距离<=maxDist），用于容错常见单字差错
    private static Set<String> approxContainsAny(String text, Set<String> dict, int maxDist) {
        Set<String> res = new HashSet<>();
        if (text == null || text.isEmpty() || dict == null || dict.isEmpty()) return res;
        for (String k : dict) {
            if (containsApprox(text, k, maxDist)) res.add(k);
        }
        return res;
    }

    private static boolean containsApprox(String text, String keyword, int maxDist) {
        if (keyword == null || keyword.isEmpty()) return false;
        if (text.contains(keyword)) return true;
        int n = keyword.length();
        // 对短词直接做全串距离判断
        if (text.length() < n) return levenshtein(text, keyword) <= maxDist;
        // 滑窗比较
        for (int i = 0; i <= text.length() - n; i++) {
            String sub = text.substring(i, i + n);
            if (levenshtein(sub, keyword) <= maxDist) return true;
        }
        // 也尝试针对插入/删除的情况扩大一个字符窗口
        if (maxDist > 0) {
            for (int i = 0; i <= text.length() - Math.max(1, n - 1); i++) {
                int win = Math.min(text.length() - i, n + 1);
                String sub = text.substring(i, i + win);
                if (levenshtein(sub, keyword) <= maxDist) return true;
            }
        }
        return false;
    }

    private static int levenshtein(String a, String b) {
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                int del = dp[i - 1][j] + 1;
                int ins = dp[i][j - 1] + 1;
                int sub = dp[i - 1][j - 1] + cost;
                dp[i][j] = Math.min(Math.min(del, ins), sub);
            }
        }
        return dp[n][m];
    }

    private static Set<String> ConcurrentHashSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    private static class Hit {
        final CarSpec spec;
        final int score;
        Hit(CarSpec spec, int score) { this.spec = spec; this.score = score; }
    }

    // ===== 矩阵型CSV解析 =====

    private static boolean isMatrixHeader(Map<String, Integer> header) {
        String first = getHeaderNameByIndex(header, 0);
        String c = canonicalizeHeader(first);
        return Arrays.asList("feature", "参数项", "项目", "字段", "配置项").contains(c);
    }

    private static String getHeaderNameByIndex(Map<String, Integer> header, int index) {
        for (Map.Entry<String, Integer> e : header.entrySet()) {
            if (e.getValue() != null && e.getValue() == index) return e.getKey();
        }
        return null;
    }

    private static String normalizeValue(String v) {
        if (isBlank(v)) return null;
        String t = sanitizeValue(v);
        switch (t) {
            case "●":
            case "√":
            case "yes":
            case "y":
            case "是":
            case "有":
                return "支持";
            case "○":
            case "选装":
                return "选装";
            case "-":
            case "×":
            case "no":
            case "n":
            case "否":
            case "无":
                return "不支持";
            default:
                return t; // 数值或文本保持原样
        }
    }

    private int parseMatrixCsv(Map<String, Integer> header, List<CSVRecord> rows, String brandFromName, String seriesFromName) {
        String firstHeader = getHeaderNameByIndex(header, 0);
        if (isBlank(firstHeader)) return 0;

        // 按序收集后续列名（产品ID/型号ID）
        TreeMap<Integer, String> idxToName = new TreeMap<>();
        for (Map.Entry<String, Integer> e : header.entrySet()) {
            if (e.getValue() == null) continue;
            if (e.getValue() == 0) continue;
            idxToName.put(e.getValue(), e.getKey());
        }
        if (idxToName.isEmpty()) return 0;

        Map<String, CarSpec> acc = new LinkedHashMap<>();
        for (CSVRecord row : rows) {
            String paramName = row.get(firstHeader);
            if (isBlank(paramName)) continue;
            for (Map.Entry<Integer, String> me : idxToName.entrySet()) {
                String modelId = me.getValue();
                String raw = row.get(modelId);
                String val = normalizeValue(raw);
                if (isBlank(val)) continue; // 空值跳过
                String brand = brandFromName;
                String series = seriesFromName;
                if (isBlank(brand) || isBlank(series)) continue; // 无法回填品牌/车系则跳过
                CarSpec spec = acc.computeIfAbsent(modelId, k -> new CarSpec(brand.trim(), series.trim(), k.trim()));
                spec.putAttribute(paramName.trim(), val);
            }
        }

        int added = 0;
        for (CarSpec spec : acc.values()) {
            String key = keyOf(spec.getBrand(), spec.getSeries(), spec.getModel());
            byKey.put(key, spec);
            brandDict.add(normalize(spec.getBrand()));
            seriesDict.add(normalize(spec.getSeries()));
            modelDict.add(normalize(spec.getModel()));
            added++;
        }
        return added;
    }
}
