package com.userservice.demo.profile.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for customer profile update requests.
 * Email and National ID cannot be changed after registration.
 */
@Data
public class UpdateCustomerRequest {

    /** Customer's updated full name */
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    /** Customer's updated phone number - must be valid Kenyan format */
    @Pattern(regexp = "^(07|01)\\d{8}$", message = "Phone number must be a valid Kenyan number")
    private String phoneNumber;

    /** Customer's updated date of birth */
    private LocalDate dateOfBirth;
}