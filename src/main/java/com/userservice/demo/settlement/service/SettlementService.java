package com.userservice.demo.settlement.service;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.auth.repository.AuthUserRepository;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.settlement.model.Settlement;
import com.userservice.demo.settlement.repository.SettlementRepository;
import com.userservice.demo.wallet.model.Wallet;
import com.userservice.demo.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for settlement operations.
 * Runs daily to move merchant pending balance to available balance.
 * Can also be triggered manually by admin.
 */
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final WalletRepository walletRepository;
    private final SettlementRepository settlementRepository;
    private final AuthUserRepository authUserRepository;

    /**
     * Scheduled daily settlement job.
     * Runs every day at midnight.
     * Moves all merchant pending balances to available balance.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void runDailySettlement() {
        System.out.println("[SETTLEMENT] Daily settlement started...");
        processSettlement();
        System.out.println("[SETTLEMENT] Daily settlement completed.");
    }

    /**
     * Manually triggered settlement - admin only.
     * Useful for testing and demo purposes.
     */
    @Transactional
    public void runManualSettlement() {
        System.out.println("[SETTLEMENT] Manual settlement triggered by admin...");
        processSettlement();
        System.out.println("[SETTLEMENT] Manual settlement completed.");
    }

    /**
     * Core settlement logic.
     * Finds all merchant wallets with pending balance and settles them.
     */
    private void processSettlement() {
        // Get all merchant auth users
        List<AuthUser> merchants = authUserRepository.findByRole(AuthUser.Role.MERCHANT);

        for (AuthUser merchant : merchants) {
            walletRepository.findByAuthUser(merchant).ifPresent(wallet -> {
                BigDecimal pendingAmount = wallet.getPendingBalance();

                if (pendingAmount.compareTo(BigDecimal.ZERO) > 0) {
                    // Move pending to available
                    wallet.setAvailableBalance(
                            wallet.getAvailableBalance().add(pendingAmount));
                    wallet.setPendingBalance(BigDecimal.ZERO);
                    walletRepository.save(wallet);

                    // Record settlement
                    Settlement settlement = new Settlement();
                    settlement.setMerchant(merchant);
                    settlement.setAmount(pendingAmount);
                    settlementRepository.save(settlement);

                    System.out.println("[NOTIFICATION] Settlement of KES " + pendingAmount +
                            " processed for merchant: " + merchant.getEmail());
                }
            });
        }
    }

    /**
     * Get all settlements for a merchant.
     *
     * @param merchantEmail email of the merchant from JWT
     * @return list of settlements
     */
    public List<Settlement> getMerchantSettlements(String merchantEmail) {
        AuthUser merchant = authUserRepository.findByEmail(merchantEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
        return settlementRepository.findByMerchant(merchant);
    }

    /**
     * Get the last settlement run time.
     * Used by admin dashboard.
     */
    public Settlement getLastSettlement() {
        return settlementRepository.findTopByOrderBySettledAtDesc();
    }
}