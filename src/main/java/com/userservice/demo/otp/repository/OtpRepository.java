package com.userservice.demo.otp.repository;

import com.userservice.demo.otp.model.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for OTP record database operations.
 * Handles storing and retrieving OTP audit records.
 */
public interface OtpRepository extends JpaRepository<OtpRecord, Long> {

    /**
     * Find the latest pending OTP for a recipient and type.
     * Used to check if an OTP already exists before generating a new one.
     */
    Optional<OtpRecord> findByRecipientAndOtpTypeAndStatus(
            String recipient,
            OtpRecord.OtpType otpType,
            OtpRecord.OtpStatus status
    );

    /**
     * Check if a pending OTP exists for a recipient and type.
     */
    boolean existsByRecipientAndOtpTypeAndStatus(
            String recipient,
            OtpRecord.OtpType otpType,
            OtpRecord.OtpStatus status
    );
}