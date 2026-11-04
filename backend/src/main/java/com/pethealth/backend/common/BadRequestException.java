package com.pethealth.backend.common;

/** Thrown when the request has invalid data. Turned into a 400 by ApiExceptionHandler. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
