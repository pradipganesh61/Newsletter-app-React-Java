package com.newsletter.signup.exception;

public class RiskyEmailException extends RuntimeException {
    public RiskyEmailException(String message) {
        super(message);
    }
}
