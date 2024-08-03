package com.newsletter.signup.exception;

public class InvalidEmailFormatException extends RuntimeException {
    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
