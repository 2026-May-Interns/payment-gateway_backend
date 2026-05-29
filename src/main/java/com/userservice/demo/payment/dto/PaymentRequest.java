package com.userservice.demo.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for creating a payment request.
 * Used by merchants to request payment from customers.
 */
@Data
public class PaymentRequest {

    /** Payment amount - must be greater than 0 */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum payment amount is KES 1")
    private BigDecimal amount;

    /** Description of what the payment is for */
    @NotBlank(message = "Description is required")
    private String description;
}