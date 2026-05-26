package com.userservice.demo.otp.dto;

import lombok.Data;

/**
 * DTO for OTP verification requests.
 * Contains the recipient (email or phone) and the OTP code to verify.
 */
@Data
public class VerifyOtpRequest {

    /** The email or phone number the OTP was sent to */
    private String recipient;

    /** The OTP type - either EMAIL or PHONE */
    private String otpType;

    /** The 6 digit OTP code submitted by the user */
    private String otpCode;
}