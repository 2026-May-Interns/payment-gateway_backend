package com.userservice.demo.user.repository;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.user.model.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for Merchant database operations.
 */
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    /** Find merchant by their auth user */
    Optional<Merchant> findByAuthUser(AuthUser authUser);

    /** Find merchant by email through auth user relationship */
    Optional<Merchant> findByAuthUser_Email(String email);

    /** Find active merchant by email - excludes soft deleted */
    Optional<Merchant> findByAuthUser_EmailAndDeletedAtIsNull(String email);

    /** Find merchant by phone number */
    Optional<Merchant> findByPhoneNumber(String phoneNumber);

    /** Find active merchant by phone - excludes soft deleted */
    Optional<Merchant> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);

    /** Check if phone number exists */
    boolean existsByPhoneNumber(String phoneNumber);

    /** Check if national ID exists */
    boolean existsByNationalId(String nationalId);

    /** Check if KRA PIN exists */
    boolean existsByKraPin(String kraPin);

    /** Check if business registration number exists */
    boolean existsByBusinessRegistrationNumber(String registrationNumber);

    /** Check if business KRA PIN exists */
    boolean existsByBusinessKraPin(String businessKraPin);

    /** Find all active merchants with pagination */
    Page<Merchant> findByDeletedAtIsNull(Pageable pageable);
}