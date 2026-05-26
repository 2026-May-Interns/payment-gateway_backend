package com.userservice.demo.user.service;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.auth.repository.AuthUserRepository;
import com.userservice.demo.exception.BadRequestException;
import com.userservice.demo.exception.DuplicateResourceException;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.otp.model.OtpRecord;
import com.userservice.demo.otp.service.OtpService;
import com.userservice.demo.security.JwtUtil;
import com.userservice.demo.user.dto.AuthResponse;
import com.userservice.demo.user.dto.LoginRequest;
import com.userservice.demo.user.dto.MerchantRegisterRequest;
import com.userservice.demo.user.dto.RegisterRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for user management operations.
 * Handles registration and login for customers, merchants and admins.
 * Authentication is centralized through AuthUser.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    /**
     * Registers a new customer account.
     * Creates AuthUser for authentication and Customer for business data.
     */
    @Transactional
    public Customer registerCustomer(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists");
        }
        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("National ID already exists");
        }
        if (customerRepository.existsByKraPin(request.getKraPin())) {
            throw new DuplicateResourceException("KRA PIN already exists");
        }
        if (!request.isTermsAccepted()) {
            throw new BadRequestException("You must accept the terms and conditions");
        }

        AuthUser authUser = new AuthUser();
        authUser.setEmail(request.getEmail());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRole(AuthUser.Role.CUSTOMER);
        AuthUser savedAuthUser = authUserRepository.save(authUser);

        Customer customer = new Customer();
        customer.setAuthUser(savedAuthUser);
        customer.setFullName(request.getFullName());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setNationalId(request.getNationalId());
        customer.setKraPin(request.getKraPin());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAccountStatus(Customer.AccountStatus.PENDING);
        customer.setTermsAccepted(true);
        customer.setTermsAcceptedAt(LocalDateTime.now());

        Customer savedCustomer = customerRepository.save(customer);

        otpService.generateOtp(savedAuthUser.getEmail(), OtpRecord.OtpType.EMAIL);
        otpService.generateOtp(savedCustomer.getPhoneNumber(), OtpRecord.OtpType.PHONE);

        return savedCustomer;
    }

    /**
     * Registers a new merchant account.
     * Creates AuthUser for authentication and Merchant for business data.
     */
    @Transactional
    public Merchant registerMerchant(MerchantRegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (merchantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists");
        }
        if (merchantRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("National ID already exists");
        }
        if (merchantRepository.existsByKraPin(request.getKraPin())) {
            throw new DuplicateResourceException("KRA PIN already exists");
        }
        if (merchantRepository.existsByBusinessRegistrationNumber(request.getBusinessRegistrationNumber())) {
            throw new DuplicateResourceException("Business registration number already exists");
        }
        if (merchantRepository.existsByBusinessKraPin(request.getBusinessKraPin())) {
            throw new DuplicateResourceException("Business KRA PIN already exists");
        }
        if (!request.isTermsAccepted()) {
            throw new BadRequestException("You must accept the terms and conditions");
        }

        AuthUser authUser = new AuthUser();
        authUser.setEmail(request.getEmail());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRole(AuthUser.Role.MERCHANT);
        AuthUser savedAuthUser = authUserRepository.save(authUser);

        Merchant merchant = new Merchant();
        merchant.setAuthUser(savedAuthUser);
        merchant.setFullName(request.getFullName());
        merchant.setPhoneNumber(request.getPhoneNumber());
        merchant.setNationalId(request.getNationalId());
        merchant.setKraPin(request.getKraPin());
        merchant.setDateOfBirth(request.getDateOfBirth());
        merchant.setBusinessName(request.getBusinessName());
        merchant.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        merchant.setBusinessKraPin(request.getBusinessKraPin());
        merchant.setBusinessType(request.getBusinessType());
        merchant.setBankName(request.getBankName());
        merchant.setBankAccountNumber(request.getBankAccountNumber());
        merchant.setBankAccountHolderName(request.getBankAccountHolderName());
        merchant.setAccountStatus(Merchant.AccountStatus.PENDING);
        merchant.setVerificationStatus(Merchant.VerificationStatus.VERIFIED);
        merchant.setTermsAccepted(true);
        merchant.setTermsAcceptedAt(LocalDateTime.now());

        Merchant savedMerchant = merchantRepository.save(merchant);

        otpService.generateOtp(savedAuthUser.getEmail(), OtpRecord.OtpType.EMAIL);
        otpService.generateOtp(savedMerchant.getPhoneNumber(), OtpRecord.OtpType.PHONE);

        return savedMerchant;
    }

    /**
     * Authenticates a user and returns JWT tokens.
     */
    public AuthResponse login(LoginRequest request) {
        AuthUser authUser = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
            handleFailedLogin(authUser);
        }

        switch (authUser.getRole()) {
            case CUSTOMER -> {
                Customer customer = customerRepository.findByAuthUser(authUser)
                        .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
                if (customer.getAccountStatus() == Customer.AccountStatus.SUSPENDED) {
                    throw new BadRequestException("Account is suspended. Contact admin to unlock");
                }
                customer.setFailedLoginAttempts(0);
                customer.setLastLoginAt(LocalDateTime.now());
                customerRepository.save(customer);
                String accessToken = jwtUtil.generateAccessToken(authUser.getEmail(), "CUSTOMER");
                String refreshToken = jwtUtil.generateRefreshToken(authUser.getEmail());
                return new AuthResponse(accessToken, refreshToken, "CUSTOMER", customer.getFullName());
            }
            case MERCHANT -> {
                Merchant merchant = merchantRepository.findByAuthUser(authUser)
                        .orElseThrow(() -> new ResourceNotFoundException("Merchant profile not found"));
                if (merchant.getAccountStatus() == Merchant.AccountStatus.SUSPENDED) {
                    throw new BadRequestException("Account is suspended. Contact admin to unlock");
                }
                merchant.setFailedLoginAttempts(0);
                merchant.setLastLoginAt(LocalDateTime.now());
                merchantRepository.save(merchant);
                String accessToken = jwtUtil.generateAccessToken(authUser.getEmail(), "MERCHANT");
                String refreshToken = jwtUtil.generateRefreshToken(authUser.getEmail());
                return new AuthResponse(accessToken, refreshToken, "MERCHANT", merchant.getFullName());
            }
            case ADMIN -> {
                String accessToken = jwtUtil.generateAccessToken(authUser.getEmail(), "ADMIN");
                String refreshToken = jwtUtil.generateRefreshToken(authUser.getEmail());
                return new AuthResponse(accessToken, refreshToken, "ADMIN", "System Admin");
            }
            default -> throw new BadRequestException("Invalid role");
        }
    }

    /**
     * Handles failed login attempts.
     */
    private void handleFailedLogin(AuthUser authUser) {
        if (authUser.getRole() == AuthUser.Role.CUSTOMER) {
            customerRepository.findByAuthUser(authUser).ifPresent(customer -> {
                int attempts = customer.getFailedLoginAttempts() + 1;
                customer.setFailedLoginAttempts(attempts);
                if (attempts >= 5) {
                    customer.setAccountStatus(Customer.AccountStatus.SUSPENDED);
                    customerRepository.save(customer);
                    throw new BadRequestException("Maximum attempts reached. Contact admin to unlock");
                }
                customerRepository.save(customer);
                throw new BadRequestException("Invalid password. " + (5 - attempts) + " attempts remaining");
            });
        }
        if (authUser.getRole() == AuthUser.Role.MERCHANT) {
            merchantRepository.findByAuthUser(authUser).ifPresent(merchant -> {
                int attempts = merchant.getFailedLoginAttempts() + 1;
                merchant.setFailedLoginAttempts(attempts);
                if (attempts >= 5) {
                    merchant.setAccountStatus(Merchant.AccountStatus.SUSPENDED);
                    merchantRepository.save(merchant);
                    throw new BadRequestException("Maximum attempts reached. Contact admin to unlock");
                }
                merchantRepository.save(merchant);
                throw new BadRequestException("Invalid password. " + (5 - attempts) + " attempts remaining");
            });
        }
        throw new BadRequestException("Invalid password");
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Not a refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtUtil.generateAccessToken(email, authUser.getRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        String fullName = switch (authUser.getRole()) {
            case CUSTOMER -> customerRepository.findByAuthUser(authUser)
                    .map(Customer::getFullName).orElse("");
            case MERCHANT -> merchantRepository.findByAuthUser(authUser)
                    .map(Merchant::getFullName).orElse("");
            case ADMIN -> "System Admin";
        };

        return new AuthResponse(newAccessToken, newRefreshToken, authUser.getRole().name(), fullName);
    }
}