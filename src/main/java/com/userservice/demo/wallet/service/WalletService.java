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

        return new WalletResponse(
                wallet.getId(),
                email,
                wallet.getAvailableBalance(),
                wallet.getPendingBalance(),
                wallet.getStatus().name(),
                wallet.getCreatedAt()
        );
    }

    /**
     * Tops up a wallet balance.
     * Mock implementation - no real payment integration.
     *
     * @param email   the email from JWT token
     * @param request contains the top up amount
     * @return updated wallet response
     */
    @Transactional
    public WalletResponse topUp(String email, TopUpRequest request) {
        Wallet wallet = walletRepository.findByAuthUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (wallet.getStatus() == Wallet.WalletStatus.INACTIVE) {
            throw new BadRequestException("Wallet is inactive");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        System.out.println("[NOTIFICATION] Top up of KES " + request.getAmount() +
                " completed for " + email);

        return new WalletResponse(
                wallet.getId(),
                email,
                wallet.getAvailableBalance(),
                wallet.getPendingBalance(),
                wallet.getStatus().name(),
                wallet.getCreatedAt()
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
}