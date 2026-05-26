package com.userservice.demo.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for merchant profile update requests.
 * Email and National ID cannot be changed after registration.
 */
@Data
public class UpdateMerchantRequest {

    /** Merchant's updated full name - must not be blank if provided */
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /** Merchant's updated phone number - must be valid Kenyan format */
    @Pattern(regexp = "^(07|01)\\d{8}$", message = "Phone number must be a valid Kenyan number")
    private String phoneNumber;

    /** Merchant's updated date of birth */
    private LocalDate dateOfBirth;

    /** Merchant's updated business name */
    @Size(min = 2, max = 100, message = "Business name must be between 2 and 100 characters")
    private String businessName;

    /** Merchant's updated bank name */
    @Size(min = 2, max = 100, message = "Bank name must be between 2 and 100 characters")
    private String bankName;

    /** Merchant's updated bank account number */
    @Size(min = 5, max = 20, message = "Bank account number must be between 5 and 20 characters")
    private String bankAccountNumber;

    /** Merchant's updated bank account holder name */
    @Size(min = 2, max = 100, message = "Bank account holder name must be between 2 and 100 characters")
    private String bankAccountHolderName;
}