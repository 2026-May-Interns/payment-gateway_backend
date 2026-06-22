package com.userservice.demo.config;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.auth.repository.AuthUserRepository;
import com.userservice.demo.payment.model.Payment;
import com.userservice.demo.payment.repository.PaymentRepository;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import com.userservice.demo.wallet.model.Wallet;
import com.userservice.demo.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeds test data on app startup.
 * Creates customers, merchants, wallets and transactions for testing.
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final AuthUserRepository authUserRepository;
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            // Only seed if no customers exist
            if (customerRepository.count() > 0) {
                System.out.println("[SEEDER] Data already exists, skipping...");
                return;
            }

            System.out.println("[SEEDER] Seeding test data...");

            // Create 3 customers
            AuthUser customerAuth1 = createAuthUser("alice@gmail.com", "Password123", AuthUser.Role.CUSTOMER);
            AuthUser customerAuth2 = createAuthUser("bob@gmail.com", "Password123", AuthUser.Role.CUSTOMER);
            AuthUser customerAuth3 = createAuthUser("charlie@gmail.com", "Password123", AuthUser.Role.CUSTOMER);

            Customer customer1 = createCustomer(customerAuth1, "Alice Wanjiru", "0712000001", "10000001", "A100000001B");
            Customer customer2 = createCustomer(customerAuth2, "Bob Kamau", "0712000002", "10000002", "A100000002B");
            Customer customer3 = createCustomer(customerAuth3, "Charlie Omondi", "0712000003", "10000003", "A100000003B");

            // Create 2 merchants
            AuthUser merchantAuth1 = createAuthUser("java@shop.com", "Password123", AuthUser.Role.MERCHANT);
            AuthUser merchantAuth2 = createAuthUser("tech@store.com", "Password123", AuthUser.Role.MERCHANT);

            Merchant merchant1 = createMerchant(merchantAuth1, "Java Shop", "0723000001", "20000001", "B200000001C", "SHOP001");
            Merchant merchant2 = createMerchant(merchantAuth2, "Tech Store", "0723000002", "20000002", "B200000002C", "SHOP002");

            // Create wallets with balances
            Wallet wallet1 = createWallet(customerAuth1, new BigDecimal("10000"));
            Wallet wallet2 = createWallet(customerAuth2, new BigDecimal("5000"));
            Wallet wallet3 = createWallet(customerAuth3, new BigDecimal("8000"));
            Wallet merchantWallet1 = createWallet(merchantAuth1, new BigDecimal("0"));
            Wallet merchantWallet2 = createWallet(merchantAuth2, new BigDecimal("0"));

            // Create 5 paid payments
            createPayment(merchantAuth1, customerAuth1, new BigDecimal("500"), "Groceries", Payment.PaymentStatus.PAID, wallet1, merchantWallet1);
            createPayment(merchantAuth1, customerAuth2, new BigDecimal("1000"), "Electronics", Payment.PaymentStatus.PAID, wallet2, merchantWallet1);
            createPayment(merchantAuth2, customerAuth1, new BigDecimal("750"), "Software subscription", Payment.PaymentStatus.PAID, wallet1, merchantWallet2);
            createPayment(merchantAuth2, customerAuth3, new BigDecimal("2000"), "Laptop repair", Payment.PaymentStatus.PAID, wallet3, merchantWallet2);
            createPayment(merchantAuth1, customerAuth3, new BigDecimal("300"), "Coffee", Payment.PaymentStatus.PAID, wallet3, merchantWallet1);

            // Create 3 pending payments
            createPendingPayment(merchantAuth1, new BigDecimal("1500"), "Office supplies");
            createPendingPayment(merchantAuth2, new BigDecimal("800"), "Phone accessories");
            createPendingPayment(merchantAuth1, new BigDecimal("450"), "Lunch");

            System.out.println("[SEEDER] Test data seeded successfully!");
            System.out.println("[SEEDER] Customers: alice@gmail.com, bob@gmail.com, charlie@gmail.com");
            System.out.println("[SEEDER] Merchants: java@shop.com, tech@store.com");
            System.out.println("[SEEDER] Password for all: Password123");
        };
    }

    private AuthUser createAuthUser(String email, String password, AuthUser.Role role) {
        AuthUser authUser = new AuthUser();
        authUser.setEmail(email);
        authUser.setPassword(passwordEncoder.encode(password));
        authUser.setRole(role);
        return authUserRepository.save(authUser);
    }

    private Customer createCustomer(AuthUser authUser, String fullName, String phone, String nationalId, String kraPin) {
        Customer customer = new Customer();
        customer.setAuthUser(authUser);
        customer.setFullName(fullName);
        customer.setPhoneNumber(phone);
        customer.setNationalId(nationalId);
        customer.setKraPin(kraPin);
        customer.setDateOfBirth(LocalDate.of(1995, 1, 1));
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
        customer.setEmailVerified(true);
        customer.setPhoneVerified(true);
        customer.setTermsAccepted(true);
        customer.setTermsAcceptedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    private Merchant createMerchant(AuthUser authUser, String businessName, String phone,
                                    String nationalId, String kraPin, String regNumber) {
        Merchant merchant = new Merchant();
        merchant.setAuthUser(authUser);
        merchant.setFullName(businessName + " Owner");
        merchant.setPhoneNumber(phone);
        merchant.setNationalId(nationalId);
        merchant.setKraPin(kraPin);
        merchant.setDateOfBirth(LocalDate.of(1990, 1, 1));
        merchant.setBusinessName(businessName);
        merchant.setBusinessRegistrationNumber(regNumber);
        merchant.setBusinessKraPin(kraPin);
        merchant.setBusinessType("Retail");
        merchant.setBankName("Equity Bank");
        merchant.setBankAccountNumber("1234567890");
        merchant.setBankAccountHolderName(businessName + " Owner");
        merchant.setAccountStatus(Merchant.AccountStatus.ACTIVE);
        merchant.setVerificationStatus(Merchant.VerificationStatus.VERIFIED);
        merchant.setEmailVerified(true);
        merchant.setPhoneVerified(true);
        merchant.setTermsAccepted(true);
        merchant.setTermsAcceptedAt(LocalDateTime.now());
        return merchantRepository.save(merchant);
    }

    private Wallet createWallet(AuthUser authUser, BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.setAuthUser(authUser);
        wallet.setAvailableBalance(balance);
        wallet.setPendingBalance(BigDecimal.ZERO);
        wallet.setStatus(Wallet.WalletStatus.ACTIVE);
        return walletRepository.save(wallet);
    }

    private void createPayment(AuthUser merchant, AuthUser customer,
                               BigDecimal amount, String description,
                               Payment.PaymentStatus status,
                               Wallet customerWallet, Wallet merchantWallet) {
        // Deduct customer wallet
        customerWallet.setAvailableBalance(customerWallet.getAvailableBalance().subtract(amount));
        walletRepository.save(customerWallet);

        // Credit merchant pending balance
        merchantWallet.setPendingBalance(merchantWallet.getPendingBalance().add(amount));
        walletRepository.save(merchantWallet);

        Payment payment = new Payment();
        payment.setMerchant(merchant);
        payment.setCustomer(customer);
        payment.setAmount(amount);
        payment.setDescription(description);
        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    private void createPendingPayment(AuthUser merchant, BigDecimal amount, String description) {
        Payment payment = new Payment();
        payment.setMerchant(merchant);
        payment.setAmount(amount);
        payment.setDescription(description);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        paymentRepository.save(payment);
    }
}