package com.userservice.demo.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO for wallet top up requests.
 */
@Data
public class TopUpRequest {

    /** Amount to top up - must be greater than 0 */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Minimum top up amount is KES 1")
    private BigDecimal amount;
}