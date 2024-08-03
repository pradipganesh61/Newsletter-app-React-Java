package com.newsletter.signup.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Newsletter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String emailAddress;

    private boolean isSubscribed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isSubscribed() {
        return isSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        isSubscribed = subscribed;
    }

    public Newsletter() {
    }

    public Newsletter(String emailAddress, boolean isSubscribed) {
        this.emailAddress = emailAddress;
        this.isSubscribed = isSubscribed;
    }

}
