package com.newsletter.signup.exception;

import com.newsletter.signup.entity.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidEmailFormatException.class)
    public ResponseEntity<ResponseObject<String>> handleInvalidEmailFormatException(InvalidEmailFormatException ex) {
        logger.warn("Invalid email format exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), false, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisposableEmailException.class)
    public ResponseEntity<ResponseObject<String>> handleDisposableEmailException(DisposableEmailException ex) {
        logger.warn("Disposable email exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.FORBIDDEN.value(), false, ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RiskyEmailException.class)
    public ResponseEntity<ResponseObject<String>> handleRiskyEmailException(RiskyEmailException ex) {
        logger.warn("Risky email exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.UNPROCESSABLE_ENTITY.value(), false, ex.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<String>> handleGlobalException(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(new ResponseObject<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), false, "An unexpected error occurred: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
