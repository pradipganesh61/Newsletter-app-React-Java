package com.newsletter.signup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class IpBlockingService {
    private static final Logger logger = LoggerFactory.getLogger(IpBlockingService.class);

    private final Map<String, Integer> ipRequestCount = new ConcurrentHashMap<>();
    private final Map<String, Long> ipBlockTime = new ConcurrentHashMap<>();

    @Value("${rate.limit.requests.per.minute}")
    private int maxRequestsPerMinute;

    @Value("${rate.limit.block.time.in.minutes}")
    private long blockTimeInMinutes;

    public boolean isBlocked(String ip) {
        if (ipBlockTime.containsKey(ip)) {
            long blockEndTime = ipBlockTime.get(ip) + TimeUnit.MINUTES.toMillis(blockTimeInMinutes);
            if (System.currentTimeMillis() > blockEndTime) {
                ipBlockTime.remove(ip);
                ipRequestCount.remove(ip);
                logger.info("Unblocking IP address: {}", ip);
                return false;
            }
            logger.warn("IP address still blocked: {}", ip);
            return true;
        }
        return false;
    }

    public void recordRequest(String ip) {
        ipRequestCount.merge(ip, 1, Integer::sum);
        int requestCount = ipRequestCount.get(ip);
        logger.info("IP address {} has made {} requests", ip, requestCount);

        if (requestCount > maxRequestsPerMinute) {
            ipBlockTime.put(ip, System.currentTimeMillis());
            logger.warn("Blocking IP address {} due to exceeding request limit", ip);
        }
    }
}

