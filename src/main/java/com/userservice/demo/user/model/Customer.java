package com.userservice.demo.user.model;

import com.userservice.demo.auth.model.AuthUser;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a customer in the payment gateway.
 * Authentication is handled by AuthUser.
 * This model stores customer specific business data.
 */
@Data
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference to the authentication user account */
    @OneToOne
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUser authUser;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String nationalId;

    @Column(nullable = false, unique = true)
    private String kraPin;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    private boolean emailVerified = false;
    private boolean phoneVerified = false;
    private int failedLoginAttempts = 0;
    private LocalDateTime lastLoginAt;
    private boolean termsAccepted = false;
    private LocalDateTime termsAcceptedAt;
    private LocalDateTime deletedAt;

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

    public enum AccountStatus {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }
}