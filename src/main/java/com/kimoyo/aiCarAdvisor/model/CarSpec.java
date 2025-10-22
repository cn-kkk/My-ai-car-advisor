package com.kimoyo.aiCarAdvisor.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class CarSpec {
    private final String brand;
    private final String series;
    private final String model;
    private final Map<String, String> attributes = new LinkedHashMap<>();

    public CarSpec(String brand, String series, String model) {
        this.brand = brand;
        this.series = series;
        this.model = model;
    }

    public String getBrand() { return brand; }
    public String getSeries() { return series; }
    public String getModel() { return model; }
    public Map<String, String> getAttributes() { return attributes; }

    public void putAttribute(String key, String value) {
        if (key == null || key.isBlank()) return;
        if (value == null || value.isBlank()) return;
        attributes.put(key, value);
    }
}