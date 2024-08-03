package com.newsletter.signup.service;

import com.newsletter.signup.config.HunterConfig;
import com.newsletter.signup.constants.CacheName;
import com.newsletter.signup.entity.HunterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailRiskAssessmentService {
    private static final Logger logger = LoggerFactory.getLogger(EmailRiskAssessmentService.class);

    @Autowired
    private HunterConfig hunterConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    @Cacheable(value = CacheName.Newsletter.EMAIL_RISKS, key = "#email")
    public boolean isRiskyEmail(String email) {
        String url = String.format("%s?email=%s&api_key=%s", hunterConfig.getApiUrl(), email, hunterConfig.getApiKey());
        logger.info("Checking email risk for: {}", email);
        try {
            ResponseEntity<HunterResponse> response = restTemplate.getForEntity(url, HunterResponse.class);
            HunterResponse responseBody = response.getBody();

            if (responseBody != null && responseBody.getData() != null) {
                boolean isRisky = responseBody.getData().isRisky();
                logger.info("Email risk check completed: {} is {}", email, isRisky ? "risky" : "not risky");
                return isRisky;
            } else {
                logger.warn("Received empty response from email risk assessment service for: {}", email);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error occurred while checking email risk for: {}", email, e);
            return false;
        }
    }
}
