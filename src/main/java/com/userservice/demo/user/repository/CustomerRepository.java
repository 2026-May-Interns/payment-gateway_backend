package com.userservice.demo.user.repository;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.user.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for Customer database operations.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /** Find customer by their auth user */
    Optional<Customer> findByAuthUser(AuthUser authUser);

    /** Find customer by email through auth user relationship */
    Optional<Customer> findByAuthUser_Email(String email);

    /** Find active customer by email - excludes soft deleted */
    Optional<Customer> findByAuthUser_EmailAndDeletedAtIsNull(String email);

    /** Find customer by phone number */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /** Find active customer by phone - excludes soft deleted */
    Optional<Customer> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);

    /** Check if phone number exists */
    boolean existsByPhoneNumber(String phoneNumber);

    /** Check if national ID exists */
    boolean existsByNationalId(String nationalId);

    /** Check if KRA PIN exists */
    boolean existsByKraPin(String kraPin);

    /** Find all active customers with pagination */
    Page<Customer> findByDeletedAtIsNull(Pageable pageable);
}