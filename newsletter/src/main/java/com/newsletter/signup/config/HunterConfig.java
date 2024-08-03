package com.newsletter.signup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HunterConfig {
    @Value("${hunter.api.url}")
    private String apiUrl;

    @Value("${hunter.api.key}")
    private String apiKey;

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }
}
