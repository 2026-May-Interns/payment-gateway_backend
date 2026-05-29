package com.userservice.demo.payment.model;

import com.userservice.demo.auth.model.AuthUser;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a payment request in the system.
 * Merchant creates a payment request.
 * Customer approves or rejects it.
 */
@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The merchant who created this payment request */
    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private AuthUser merchant;

    /** The customer who will pay - set when customer approves */
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private AuthUser customer;

    /** Payment amount */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /** Description of what the payment is for */
    @Column(nullable = false)
    private String description;

    /** Current status of the payment */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

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

    public enum PaymentStatus {
        PENDING, PAID, FAILED
    }
}