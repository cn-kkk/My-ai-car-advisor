package com.kimoyo.aiCarAdvisor.service;

import com.kimoyo.aiCarAdvisor.model.CarSpec;

import java.util.Optional;

public interface CarSpecService {
    Optional<CarSpec> findByMessage(String userText);
    String format(CarSpec spec);
    boolean isSpecQuery(String userText);
}