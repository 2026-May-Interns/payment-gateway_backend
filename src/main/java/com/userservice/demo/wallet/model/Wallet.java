package com.userservice.demo.wallet.model;

import com.userservice.demo.auth.model.AuthUser;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a wallet in the payment gateway.
 * Each customer and merchant has one wallet.
 * Merchants have both available and pending balance.
 */
@Data
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The auth user this wallet belongs to */
    @OneToOne
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUser authUser;

    /** Available balance - can be used for payments */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    /**
     * Pending balance - for merchants only.
     * Holds funds received from payments before settlement.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    /** Wallet status */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus status = WalletStatus.ACTIVE;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum WalletStatus {
        ACTIVE, INACTIVE
    }
    /** Number of top-ups done today */
    private int dailyTopUpCount = 0;

    /** Total amount topped up today in KES */
    @Column(precision = 19, scale = 2)
    private BigDecimal dailyTopUpTotal = BigDecimal.ZERO;

    /** Date of last top up - used to reset daily counters */
    private LocalDate lastTopUpDate;
}