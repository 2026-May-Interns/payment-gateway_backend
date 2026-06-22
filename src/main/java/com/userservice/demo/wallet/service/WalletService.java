package com.userservice.demo.wallet.service;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.auth.repository.AuthUserRepository;
import com.userservice.demo.exception.BadRequestException;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.wallet.dto.TopUpRequest;
import com.userservice.demo.wallet.dto.WalletResponse;
import com.userservice.demo.wallet.model.Wallet;
import com.userservice.demo.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service for wallet operations.
 * Handles wallet creation, balance queries and top ups.
 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AuthUserRepository authUserRepository;
    /** Maximum top-ups allowed per day */
    private static final int MAX_DAILY_TOPUP_COUNT = 5;

    /** Maximum total top up amount per day in KES */
    private static final BigDecimal MAX_DAILY_TOPUP_AMOUNT = new BigDecimal("300000");

    /** Maximum single top up amount in KES */
    private static final BigDecimal MAX_SINGLE_TOPUP_AMOUNT = new BigDecimal("150000");


    /**
     * Creates a wallet for a user.
     * Called automatically on registration.
     *
     * @param authUser the auth user to create wallet for
     * @return the created wallet
     */
    public Wallet createWallet(AuthUser authUser) {
        if (walletRepository.existsByAuthUser(authUser)) {
            throw new BadRequestException("Wallet already exists for this user");
        }
        Wallet wallet = new Wallet();
        wallet.setAuthUser(authUser);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setPendingBalance(BigDecimal.ZERO);
        wallet.setStatus(Wallet.WalletStatus.ACTIVE);

        System.out.println("[WALLET] Wallet created for: " + authUser.getEmail());
        return walletRepository.save(wallet);
    }

    /**
     * Saves a wallet - used internally by payment service.
     */
    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    /**
     * Gets wallet details for the logged-in user.
     *
     * @param email the email from JWT token
     * @return wallet response DTO
     */
    public WalletResponse getWallet(String email) {
        Wallet wallet = walletRepository.findByAuthUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // Reset daily counters if last top up was not today
        resetDailyCountersIfNeeded(wallet);

        int topUpsRemaining = MAX_DAILY_TOPUP_COUNT - wallet.getDailyTopUpCount();
        BigDecimal amountRemaining = MAX_DAILY_TOPUP_AMOUNT.subtract(wallet.getDailyTopUpTotal());

        return new WalletResponse(
                wallet.getId(),
                email,
                wallet.getAvailableBalance(),
                wallet.getPendingBalance(),
                wallet.getStatus().name(),
                wallet.getCreatedAt(),
                topUpsRemaining,
                amountRemaining
        );
    }

    /**
     * Tops up a wallet balance.
     * Mock implementation - no real payment integration.
     *
     * @param email   the email from JWT token
     * @param request contains the top-up amount
     * @return updated wallet response
     */
    @Transactional
    public WalletResponse topUp(String email, TopUpRequest request) {
        Wallet wallet = walletRepository.findByAuthUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (wallet.getStatus() == Wallet.WalletStatus.INACTIVE) {
            throw new BadRequestException("Wallet is inactive");
        }

        // Reset daily counters if needed
        resetDailyCountersIfNeeded(wallet);

        // Check single top up limit
        if (request.getAmount().compareTo(MAX_SINGLE_TOPUP_AMOUNT) > 0) {
            throw new BadRequestException("Maximum single top up amount is KES 150,000");
        }

        // Check daily count limit
        if (wallet.getDailyTopUpCount() >= MAX_DAILY_TOPUP_COUNT) {
            throw new BadRequestException("You have reached the maximum of 5 top ups per day");
        }

        // Check daily amount limit
        BigDecimal newDailyTotal = wallet.getDailyTopUpTotal().add(request.getAmount());
        if (newDailyTotal.compareTo(MAX_DAILY_TOPUP_AMOUNT) > 0) {
            BigDecimal remaining = MAX_DAILY_TOPUP_AMOUNT.subtract(wallet.getDailyTopUpTotal());
            throw new BadRequestException("Daily top up limit exceeded. You can only top up KES " + remaining + " more today");
        }

        // Update wallet
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
        wallet.setDailyTopUpCount(wallet.getDailyTopUpCount() + 1);
        wallet.setDailyTopUpTotal(wallet.getDailyTopUpTotal().add(request.getAmount()));
        wallet.setLastTopUpDate(java.time.LocalDate.now());
        walletRepository.save(wallet);

        int topUpsRemaining = MAX_DAILY_TOPUP_COUNT - wallet.getDailyTopUpCount();
        BigDecimal amountRemaining = MAX_DAILY_TOPUP_AMOUNT.subtract(wallet.getDailyTopUpTotal());

        System.out.println("[NOTIFICATION] Top up of KES " + request.getAmount() +
                " completed for " + email +
                ". Top ups remaining today: " + topUpsRemaining);

        return new WalletResponse(
                wallet.getId(),
                email,
                wallet.getAvailableBalance(),
                wallet.getPendingBalance(),
                wallet.getStatus().name(),
                wallet.getCreatedAt(),
                topUpsRemaining,
                amountRemaining
        );
    }


    /**
     * Gets wallet by auth user - used internally by payment service.
     *
     * @param authUser the auth user
     * @return the wallet
     */
    public Wallet getWalletByAuthUser(AuthUser authUser) {
        return walletRepository.findByAuthUser(authUser)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }
    /**
     * Resets daily top up counters if last top up was not today.
     */
    private void resetDailyCountersIfNeeded(Wallet wallet) {
        if (wallet.getLastTopUpDate() == null ||
                !wallet.getLastTopUpDate().equals(java.time.LocalDate.now())) {
            wallet.setDailyTopUpCount(0);
            wallet.setDailyTopUpTotal(BigDecimal.ZERO);
            walletRepository.save(wallet);
        }
    }
}