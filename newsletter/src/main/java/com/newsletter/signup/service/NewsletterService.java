package com.newsletter.signup.service;

import com.newsletter.signup.entity.Newsletter;
import com.newsletter.signup.exception.DisposableEmailException;
import com.newsletter.signup.exception.InvalidEmailFormatException;
import com.newsletter.signup.exception.RiskyEmailException;
import com.newsletter.signup.repository.NewsletterRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NewsletterService {

    private static final Logger logger = LoggerFactory.getLogger(NewsletterService.class);

    @Autowired
    private NewsletterRepository newsletterRepository;

    @Autowired
    private EmailRiskAssessmentService emailRiskAssessmentService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$");

    private Set<String> disposableDomains = new HashSet<>();


    public List<Newsletter> getAllNewsletterUsers() {
        logger.info("Fetching all newsletter users");
        List<Newsletter> users = newsletterRepository.findAll();
        logger.info("Fetched {} newsletter users", users.size());
        return users;
    }

    @PostConstruct
    public void loadDisposableEmailBlockList() throws IOException {
        ClassPathResource resource = new ClassPathResource("disposable_email_blocklist.conf");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                disposableDomains.add(line.trim());
            }
        }
        logger.info("Loaded disposable email blocklist with {} domains", disposableDomains.size());
    }
    public boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isDisposableEmail(String email) {
        String domain = email.substring(email.indexOf("@") + 1);
        return disposableDomains.contains(domain);
    }

    public String processEmail(String email, boolean subscribe) {
        if (!isValidEmail(email)) {
            logger.warn("Invalid email format: {}", email);
            throw new InvalidEmailFormatException("Invalid email format.");
        }

        if (isDisposableEmail(email)) {
            logger.warn("Disposable email address: {}", email);
            throw new DisposableEmailException("Disposable email addresses are not allowed.");
        }

        if (emailRiskAssessmentService.isRiskyEmail(email)) {
            logger.warn("Risky email address: {}", email);
            throw new RiskyEmailException("Email address is considered risky");
        }

        Newsletter existingNewsletter = newsletterRepository.findByEmailAddress(email);

        if (existingNewsletter == null) {
            if (subscribe) {
                Newsletter newNewsletter = new Newsletter();
                newNewsletter.setEmailAddress(email);
                newNewsletter.setSubscribed(true);
                newsletterRepository.save(newNewsletter);
                logger.info("New subscription successful for email: {}", email);
                return "Subscription successful";
            } else {
                logger.info("Unsubscribe attempt for non-existing email: {}", email);
                return "You are not subscribed.";
            }
        } else {
            if (subscribe) {
                if (existingNewsletter.isSubscribed()) {
                    logger.info("Already subscribed: {}", email);
                    return "Already subscribed";
                } else {
                    existingNewsletter.setSubscribed(true);
                    newsletterRepository.save(existingNewsletter);
                    logger.info("Subscription successful for previously unsubscribed email: {}", email);
                    return "Subscription successful";
                }
            } else {
                if (existingNewsletter.isSubscribed()) {
                    existingNewsletter.setSubscribed(false);
                    newsletterRepository.save(existingNewsletter);
                    logger.info("Unsubscription successful for email: {}", email);
                    return "Unsubscription successful";
                } else {
                    logger.info("Already unsubscribed: {}", email);
                    return "Already unsubscribed";
                }
            }
        }
    }
}

