package com.userservice.demo.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for wallet response.
 * Hides internal fields and returns only what the client needs.
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
}