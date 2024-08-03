package com.newsletter.signup.repository;

import com.newsletter.signup.entity.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    Newsletter findByEmailAddress(String emailAddress);
}
