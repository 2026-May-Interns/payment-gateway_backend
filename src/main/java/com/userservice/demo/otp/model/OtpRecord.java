package com.userservice.demo.otp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents an OTP record stored in the database.
 * Tracks the status of OTPs sent for email and phone verification.
 * Actual OTP codes are stored in Redis for fast lookup and auto-expiry.
 */
@Data
@Entity
@Table(name = "otp_records")
public class OtpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The email or phone number the OTP was sent to */
    @Column(nullable = false)
    private String recipient;

    /** The type of OTP - either EMAIL or PHONE */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType otpType;

    /** The current status of this OTP record */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpStatus status = OtpStatus.PENDING;

    /** When this OTP was created */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** When this OTP expires */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /** When this OTP was used or expired */
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** OTP can be sent to email or phone */
    public enum OtpType {
        EMAIL, PHONE
    }

    /** Tracks whether OTP has been used or expired */
    public enum OtpStatus {
        PENDING, USED, EXPIRED
    }
}