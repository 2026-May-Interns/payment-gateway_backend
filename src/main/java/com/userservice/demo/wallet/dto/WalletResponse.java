package com.userservice.demo.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for wallet response.
 * Includes balance and daily top up limit information.
 */
@Data
@AllArgsConstructor
public class WalletResponse {

    private Long id;
    private String email;
    private BigDecimal availableBalance;
    private BigDecimal pendingBalance;
    private String status;
    private LocalDateTime createdAt;

    /** How many top-ups remaining today */
    private int topUpsRemainingToday;

    /** How much more can be topped up today in KES */
    private BigDecimal amountRemainingToday;
}