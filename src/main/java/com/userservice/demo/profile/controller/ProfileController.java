package com.userservice.demo.profile.controller;

import com.userservice.demo.profile.dto.UpdateCustomerRequest;
import com.userservice.demo.profile.dto.UpdateMerchantRequest;
import com.userservice.demo.profile.service.ProfileService;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;import jakarta.validation.Valid;

/**
 * Controller for profile update endpoints.
 * Each role can only update their own profile.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Updates a customer's profile.
     * Only accessible by CUSTOMER role.
     * Email extracted from JWT token.
     */
    @PutMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Customer> updateCustomerProfile(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(profileService.updateCustomerProfile(email, request));
    }

    /**
     * Updates a merchant's profile.
     * Only accessible by MERCHANT role.
     * Email extracted from JWT token.
     */
    @PutMapping("/merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<Merchant> updateMerchantProfile(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody UpdateMerchantRequest request) {
        return ResponseEntity.ok(profileService.updateMerchantProfile(email, request));
    }
}