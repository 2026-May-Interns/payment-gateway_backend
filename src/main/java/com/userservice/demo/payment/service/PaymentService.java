package com.userservice.demo.payment.service;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.auth.repository.AuthUserRepository;
import com.userservice.demo.exception.BadRequestException;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.payment.dto.PaymentRequest;
import com.userservice.demo.payment.dto.PaymentResponse;
import com.userservice.demo.payment.model.Payment;
import com.userservice.demo.payment.repository.PaymentRepository;
import com.userservice.demo.wallet.model.Wallet;
import com.userservice.demo.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for payment operations.
 * Handles payment request creation, approval and rejection.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuthUserRepository authUserRepository;
    private final WalletService walletService;

    /**
     * Merchant creates a payment request.
     *
     * @param merchantEmail email of the merchant from JWT
     * @param request       payment details
     * @return payment response
     */
    public PaymentResponse createPaymentRequest(String merchantEmail, PaymentRequest request) {
        AuthUser merchant = authUserRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        if (merchant.getRole() != AuthUser.Role.MERCHANT) {
            throw new BadRequestException("Only merchants can create payment requests");
        }

        Payment payment = new Payment();
        payment.setMerchant(merchant);
        payment.setAmount(request.getAmount());
        payment.setDescription(request.getDescription());
        payment.setStatus(Payment.PaymentStatus.PENDING);

        Payment saved = paymentRepository.save(payment);

        System.out.println("[NOTIFICATION] Payment request of KES " + request.getAmount() +
                " created by " + merchantEmail);

        return mapToResponse(saved);
    }

    /**
     * Get pending payments for a merchant.
     */
    public List<PaymentResponse> getMerchantPendingPayments(String merchantEmail) {
        AuthUser merchant = authUserRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        return paymentRepository.findByMerchantAndStatus(merchant, Payment.PaymentStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }



    /**
     * Get all pending payment requests - for customers to see.
     *
     * @return list of pending payments
     */
    public List<PaymentResponse> getPendingPayments() {
        return paymentRepository.findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Customer approves a payment request.
     * Deducts customer wallet and credits merchant pending balance.
     *
     * @param customerEmail email of the customer from JWT
     * @param paymentId     the payment to approve
     * @return updated payment response
     */
    @Transactional
    public PaymentResponse approvePayment(String customerEmail, Long paymentId) {
        AuthUser customer = authUserRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new BadRequestException("Payment is no longer pending");
        }

        // Get customer wallet and check balance
        Wallet customerWallet = walletService.getWalletByAuthUser(customer);

        if (customerWallet.getAvailableBalance().compareTo(payment.getAmount()) < 0) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BadRequestException("Insufficient balance");
        }

        // Deduct customer wallet
        customerWallet.setAvailableBalance(
                customerWallet.getAvailableBalance().subtract(payment.getAmount()));

        // Credit merchant pending balance
        Wallet merchantWallet = walletService.getWalletByAuthUser(payment.getMerchant());
        merchantWallet.setPendingBalance(
                merchantWallet.getPendingBalance().add(payment.getAmount()));

        // Save wallets
        walletService.saveWallet(customerWallet);
        walletService.saveWallet(merchantWallet);

        // Update payment
        payment.setCustomer(customer);
        payment.setStatus(Payment.PaymentStatus.PAID);
        Payment saved = paymentRepository.save(payment);

        System.out.println("[NOTIFICATION] Payment of KES " + payment.getAmount() +
                " completed. Customer: " + customerEmail +
                " → Merchant: " + payment.getMerchant().getEmail());

        return mapToResponse(saved);
    }

    /**
     * Customer rejects a payment request.
     *
     * @param customerEmail email of the customer from JWT
     * @param paymentId     the payment to reject
     * @return updated payment response
     */
    public PaymentResponse rejectPayment(String customerEmail, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new BadRequestException("Payment is no longer pending");
        }

        payment.setStatus(Payment.PaymentStatus.FAILED);
        Payment saved = paymentRepository.save(payment);

        System.out.println("[NOTIFICATION] Payment of KES " + payment.getAmount() +
                " rejected by " + customerEmail);

        return mapToResponse(saved);
    }

    /**
     * Get all payments for a merchant.
     *
     * @param merchantEmail email of the merchant from JWT
     * @return list of merchant payments
     */
    public List<PaymentResponse> getMerchantPayments(String merchantEmail) {
        AuthUser merchant = authUserRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
        return paymentRepository.findByMerchantOrderByCreatedAtDesc(merchant)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get paid payments for a merchant.
     */
    public List<PaymentResponse> getMerchantPaidPayments(String merchantEmail) {
        AuthUser merchant = authUserRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        return paymentRepository.findByMerchantAndStatus(merchant, Payment.PaymentStatus.PAID)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get rejected payments for a merchant.
     */
    public List<PaymentResponse> getMerchantRejectedPayments(String merchantEmail) {
        AuthUser merchant = authUserRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        return paymentRepository.findByMerchantAndStatus(merchant, Payment.PaymentStatus.FAILED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    /**
     * Get all payments for a customer.
     *
     * @param customerEmail email of the customer from JWT
     * @return list of customer payments
     */
    public List<PaymentResponse> getCustomerPayments(String customerEmail) {
        AuthUser customer = authUserRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return paymentRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Maps Payment entity to PaymentResponse DTO.
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getMerchant().getEmail(),
                payment.getCustomer() != null ? payment.getCustomer().getEmail() : null,
                payment.getAmount(),
                payment.getDescription(),
                payment.getStatus().name(),
                payment.getCreatedAt()
        );
    }

}