package com.userservice.demo.user.model;

import com.userservice.demo.auth.model.AuthUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a merchant in the payment gateway.
 * Authentication is handled by AuthUser.
 * This model stores merchant specific business data.
 */
@Data
@Entity
@Table(name = "merchants")
public class Merchant {

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

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String businessRegistrationNumber;

    @Column(nullable = false, unique = true)
    private String businessKraPin;

    @Column(nullable = false)
    private String businessType;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(nullable = false)
    private String bankAccountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.VERIFIED;

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

    public void setEmail(@NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email) {
    }

    public void setPassword(String encode) {
    }

    public enum VerificationStatus {
        PENDING, VERIFIED, REJECTED
    }

    public enum AccountStatus {
        PENDING, ACTIVE, SUSPENDED, CLOSED
    }
}