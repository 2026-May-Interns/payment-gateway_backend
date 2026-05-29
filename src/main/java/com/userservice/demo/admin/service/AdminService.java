package com.userservice.demo.admin.service;

import com.userservice.demo.exception.BadRequestException;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;import com.userservice.demo.wallet.repository.WalletRepository;
import com.userservice.demo.payment.repository.PaymentRepository;
import com.userservice.demo.settlement.repository.SettlementRepository;
import com.userservice.demo.settlement.model.Settlement;
import java.util.Map;
import java.util.HashMap;

/**
 * Service for admin operations.
 * Handles customer and merchant management including
 * suspension, unlocking, soft deletion and pagination.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;
    private final SettlementRepository settlementRepository;

    /**
     * Returns paginated list of active customers.
     */
    public Page<Customer> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findByDeletedAtIsNull(pageable);
    }

    /**
     * Returns a single customer by ID.
     */
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    /**
     * Suspends a customer account.
     * Throws exception if account is already suspended.
     */
    public void suspendCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (customer.getAccountStatus() == Customer.AccountStatus.SUSPENDED) {
            throw new BadRequestException("Customer account is already suspended");
        }
        customer.setAccountStatus(Customer.AccountStatus.SUSPENDED);
        customerRepository.save(customer);
    }

    /**
     * Unsuspends a customer account.
     * Throws exception if account is not suspended.
     */
    public void unsuspendCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (customer.getAccountStatus() != Customer.AccountStatus.SUSPENDED) {
            throw new BadRequestException("Customer account is not suspended");
        }
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
        customerRepository.save(customer);
    }

    /**
     * Unlocks a locked customer account.
     * Throws exception if account is not suspended.
     */
    public void unlockCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (customer.getAccountStatus() != Customer.AccountStatus.SUSPENDED) {
            throw new BadRequestException("Customer account is not locked");
        }
        customer.setFailedLoginAttempts(0);
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
        customerRepository.save(customer);
    }

    /**

    /**
     * Soft deletes a customer account.
     * Sets deletedAt timestamp instead of removing from database.
     */
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customer.setDeletedAt(LocalDateTime.now());
        customer.setAccountStatus(Customer.AccountStatus.CLOSED);
        customerRepository.save(customer);
    }

    /**
     * Returns paginated list of active merchants.
     */
    public Page<Merchant> getAllMerchants(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return merchantRepository.findByDeletedAtIsNull(pageable);
    }

    /**
     * Returns a single merchant by ID.
     */
    public Merchant getMerchantById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));
    }

    /**
     * Suspends a merchant account.
     * Throws exception if account is already suspended.
     */
    public void suspendMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        if (merchant.getAccountStatus() == Merchant.AccountStatus.SUSPENDED) {
            throw new BadRequestException("Merchant account is already suspended");
        }
        merchant.setAccountStatus(Merchant.AccountStatus.SUSPENDED);
        merchantRepository.save(merchant);
    }


    /**
     * Unsuspends a merchant account.
     * Throws exception if account is not suspended.
     */
    public void unsuspendMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        if (merchant.getAccountStatus() != Merchant.AccountStatus.SUSPENDED) {
            throw new BadRequestException("Merchant account is not suspended");
        }
        merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);
        merchantRepository.save(merchant);
    }

    /**
     * Unlocks a locked merchant account.
     * Throws exception if account is not suspended.
     */
    public void unlockMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        if (merchant.getAccountStatus() != Merchant.AccountStatus.SUSPENDED) {
            throw new BadRequestException("Merchant account is not locked");
        }
        merchant.setFailedLoginAttempts(0);
        merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);
        merchantRepository.save(merchant);
    }

    /**
     * Soft deletes a merchant account.
     */
    public void deleteMerchant(Long id) {
        Merchant merchant = getMerchantById(id);
        merchant.setDeletedAt(LocalDateTime.now());
        merchant.setAccountStatus(Merchant.AccountStatus.CLOSED);
        merchantRepository.save(merchant);
    }

    /**
     * Returns system overview stats for admin dashboard.
     * Shows total users, wallets, transactions and last settlement.
     */
    public Map<String, Object> getSystemOverview() {
        Map<String, Object> overview = new HashMap<>();

        // Total customers
        overview.put("totalCustomers", customerRepository.count());

        // Total merchants
        overview.put("totalMerchants", merchantRepository.count());

        // Total wallets
        overview.put("totalWallets", walletRepository.count());

        // Total transactions
        overview.put("totalPayments", paymentRepository.count());

        // Last settlement
        Settlement lastSettlement = settlementRepository.findTopByOrderBySettledAtDesc();
        overview.put("lastSettlementRun", lastSettlement != null ? lastSettlement.getSettledAt() : "No settlements yet");

        return overview;
    }
}