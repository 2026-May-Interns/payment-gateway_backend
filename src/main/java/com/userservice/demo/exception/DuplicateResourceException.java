package com.userservice.demo.exception;

/**
 * Thrown when attempting to create a resource that already exists.
 * Maps to HTTP 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}