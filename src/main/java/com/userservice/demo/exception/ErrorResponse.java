package com.userservice.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Standard error response structure returned for all API errors.
 * Provides consistent error format for consuming clients.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    /** HTTP status code */
    private int status;

    /** Short error type description */
    private String error;

    /** Detailed error message */
    private String message;

    /** Timestamp when error occurred */
    private LocalDateTime timestamp;
}