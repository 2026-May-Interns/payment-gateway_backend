package com.userservice.demo.otp.controller;

import com.userservice.demo.otp.dto.VerifyOtpRequest;
import com.userservice.demo.otp.model.OtpRecord;
import com.userservice.demo.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for OTP verification endpoints.
 * Handles email and phone OTP verification requests.
 */
@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    /**
     * Verifies an OTP submitted by the user.
     * Marks the OTP as used in the database and deletes it from Redis.
     */
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        otpService.verifyOtp(request);
        return ResponseEntity.ok("OTP verified successfully");
    }
    /**
     * Resends a fresh OTP to the given recipient.
     * Expires any existing OTP before generating a new one.
     */
    @PostMapping("/resend")
    public ResponseEntity<String> resendOtp(@RequestBody VerifyOtpRequest request) {
        OtpRecord.OtpType otpType = OtpRecord.OtpType.valueOf(request.getOtpType().toUpperCase());
        otpService.resendOtp(request.getRecipient(), otpType);
        return ResponseEntity.ok("OTP resent successfully");
    }
}