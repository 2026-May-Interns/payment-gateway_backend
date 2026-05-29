package com.userservice.demo.payment.controller;

import com.userservice.demo.payment.dto.PaymentRequest;
import com.userservice.demo.payment.dto.PaymentResponse;
import com.userservice.demo.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for payment endpoints.
 * Merchants create payment requests.
 * Customers approve or reject them.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Merchant creates a payment request.
     */
    @PostMapping("/request")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<PaymentResponse> createPaymentRequest(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPaymentRequest(email, request));
    }

    /**
     * Get all pending payment requests - for customers.
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PaymentResponse>> getPendingPayments() {
        return ResponseEntity.ok(paymentService.getPendingPayments());
    }

    /**
     * Customer approves a payment request.
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> approvePayment(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.approvePayment(email, id));
    }

    /**
     * Customer rejects a payment request.
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> rejectPayment(
            @AuthenticationPrincipal String email,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.rejectPayment(email, id));
    }

    /**
     * Merchant gets their payment history.
     */
    @GetMapping("/merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<List<PaymentResponse>> getMerchantPayments(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(paymentService.getMerchantPayments(email));
    }

    /**
     * Customer gets their payment history.
     */
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PaymentResponse>> getCustomerPayments(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(paymentService.getCustomerPayments(email));
    }
}