package com.userservice.demo.wallet.controller;

import com.userservice.demo.wallet.dto.TopUpRequest;
import com.userservice.demo.wallet.dto.WalletResponse;
import com.userservice.demo.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for wallet endpoints.
 * Customers and merchants can view and top up their wallets.
 */
@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    /**
     * Get wallet balance for logged-in user.
     * Accessible by both CUSTOMER and MERCHANT roles.
     */
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('MERCHANT')")
    public ResponseEntity<WalletResponse> getWallet(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(walletService.getWallet(email));
    }

    /**
     * Top up wallet balance.
     * Accessible by CUSTOMER only.
     */
    @PostMapping("/topup")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WalletResponse> topUp(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody TopUpRequest request) {
        return ResponseEntity.ok(walletService.topUp(email, request));
    }
}