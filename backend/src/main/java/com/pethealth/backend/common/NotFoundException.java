package com.pethealth.backend.common;

/** Thrown when a requested resource doesn't exist. Turned into a 404 by ApiExceptionHandler. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
