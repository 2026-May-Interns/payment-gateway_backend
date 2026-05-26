package com.userservice.demo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import com.userservice.demo.exception.ValidAge;

@Data
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;


    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(07|01)\\d{8}$", message = "Phone number must be a valid 10 digit Kenyan number")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "National ID is required")
    @Pattern(regexp = "^\\d{7,8}$", message = "National ID must be 7 or 8 digits")
    private String nationalId;

    @NotBlank(message = "KRA PIN is required")
    @Pattern(regexp = "^[A-Z]\\d{9}[A-Z]$", message = "KRA PIN must be in format A123456789B")
    private String kraPin;

    @ValidAge
    private LocalDate dateOfBirth;
    private boolean termsAccepted;

}
