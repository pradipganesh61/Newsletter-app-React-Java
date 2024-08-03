package com.newsletter.signup.controller;

import com.newsletter.signup.constants.API;
import com.newsletter.signup.entity.EmailRequest;
import com.newsletter.signup.entity.Newsletter;
import com.newsletter.signup.entity.ResponseObject;
import com.newsletter.signup.exception.DisposableEmailException;
import com.newsletter.signup.exception.InvalidEmailFormatException;
import com.newsletter.signup.exception.RiskyEmailException;
import com.newsletter.signup.service.IpBlockingService;
import com.newsletter.signup.service.NewsletterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bucket4j.Bucket;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(API.Newsletter.BASE_URL)
public class NewsletterController {

    private static final Logger logger = LoggerFactory.getLogger(NewsletterController.class);


    @Autowired
    private NewsletterService newsletterService;

    @Autowired
    private Bucket bucket;

    @Autowired
    private IpBlockingService ipBlockingService;

    @GetMapping(API.Newsletter.GET_NEWSLETTER_USERS)
    public ResponseEntity<ResponseObject<List<Newsletter>>> getAllNewsletterUsers() {
        logger.info("Received request to fetch all newsletter users");
        List<Newsletter> newslettersUsers = newsletterService.getAllNewsletterUsers();
        logger.info("Returning {} newsletter users", newslettersUsers.size());
        return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK.value(), true, "Fetched all newsletter users", newslettersUsers));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ResponseObject<String>> subscribe(@RequestBody EmailRequest emailRequest, HttpServletRequest request) {
        logger.info("Received subscription request for email: {}", emailRequest.getEmail());
        return handleSubscription(emailRequest.getEmail(), request, true);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<ResponseObject<String>> unsubscribe(@RequestBody EmailRequest emailRequest, HttpServletRequest request) {
        logger.info("Received unsubscription request for email: {}", emailRequest.getEmail());
        return handleSubscription(emailRequest.getEmail(), request, false);
    }

    private ResponseEntity<ResponseObject<String>> handleSubscription(String email, HttpServletRequest request, boolean isSubscribe) {
        String clientIp = request.getRemoteAddr();
        try {
            if (ipBlockingService.isBlocked(clientIp)) {
                logger.warn("Blocked IP address: {}", clientIp);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new ResponseObject<>(HttpStatus.TOO_MANY_REQUESTS.value(), false, "Too many requests. Please wait for few seconds or reload"));
            }
            ipBlockingService.recordRequest(clientIp);

            if (bucket.tryConsume(1)) {
                String response = newsletterService.processEmail(email, isSubscribe);
                logger.info("{} request from IP {}: {}", isSubscribe ? "Subscription" : "Unsubscription", clientIp, email);
                return ResponseEntity.ok(new ResponseObject<>(HttpStatus.OK.value(), true, response));
            } else {
                logger.warn("Rate limit exceeded for IP: {}", clientIp);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(new ResponseObject<>(HttpStatus.TOO_MANY_REQUESTS.value(), false, "Too many requests"));
            }
        } catch (InvalidEmailFormatException | DisposableEmailException | RiskyEmailException ex) {
            logger.error("Error processing {} request for email {}: {}", isSubscribe ? "subscription" : "unsubscription", email, ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), false, ex.getMessage(), null));
        } catch (Exception e) {
            logger.error("Error processing {} request for email {}: {}", isSubscribe ? "subscription" : "unsubscription", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An error occurred. Please try again later."));
        }
    }

}
