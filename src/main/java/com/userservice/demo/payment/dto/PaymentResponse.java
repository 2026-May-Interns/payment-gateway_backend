package com.userservice.demo.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment response.
 * Returns payment details to the client.
 */
@Data
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String merchantEmail;
    private String customerEmail;
    private BigDecimal amount;
    private String description;
    private String status;
    private LocalDateTime createdAt;
}