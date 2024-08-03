package com.newsletter.signup.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.bucket4j.Bucket4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    @Value("${bucket4j.limit}")
    private int bucket4jRequestsLimit;

    @Value("${bucket4j.refill.interval}")
    private int refillIntervalMinutes;

    @Bean
    public Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(bucket4jRequestsLimit, Refill.greedy(bucket4jRequestsLimit, Duration.ofMinutes(refillIntervalMinutes)));
        return Bucket4j.builder().addLimit(limit).build();
    }
}
