package com.userservice.demo.profile.service;

import com.userservice.demo.exception.DuplicateResourceException;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.profile.dto.UpdateCustomerRequest;
import com.userservice.demo.profile.dto.UpdateMerchantRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for profile update operations.
 * Handles updating customer and merchant profile details.
 * Email and National ID cannot be updated.
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    /**
     * Updates a customer's profile details.
     * Only fullName, phoneNumber and dateOfBirth can be updated.
     */
    public Customer updateCustomerProfile(String email, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findByAuthUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().isEmpty() &&
                !request.getPhoneNumber().equals(customer.getPhoneNumber()) &&
                customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already in use");
        }

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            customer.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            customer.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            customer.setDateOfBirth(request.getDateOfBirth());
        }

        return customerRepository.save(customer);
    }

    /**
     * Updates a merchant's profile details.
     * Only fullName, phoneNumber, dateOfBirth and business details can be updated.
     */
    public Merchant updateMerchantProfile(String email, UpdateMerchantRequest request) {
        Merchant merchant = merchantRepository.findByAuthUser_Email(email)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().isEmpty() &&
                !request.getPhoneNumber().equals(merchant.getPhoneNumber()) &&
                merchantRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already in use");
        }

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            merchant.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            merchant.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            merchant.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getBusinessName() != null && !request.getBusinessName().isEmpty()) {
            merchant.setBusinessName(request.getBusinessName());
        }
        if (request.getBankName() != null && !request.getBankName().isEmpty()) {
            merchant.setBankName(request.getBankName());
        }
        if (request.getBankAccountNumber() != null && !request.getBankAccountNumber().isEmpty()) {
            merchant.setBankAccountNumber(request.getBankAccountNumber());
        }
        if (request.getBankAccountHolderName() != null && !request.getBankAccountHolderName().isEmpty()) {
            merchant.setBankAccountHolderName(request.getBankAccountHolderName());
        }

        return merchantRepository.save(merchant);
    }
}