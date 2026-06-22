package com.userservice.demo.payment.repository;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for Payment database operations.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Find all payments created by a merchant */
    List<Payment> findByMerchant(AuthUser merchant);

    /** Find all pending payments for a customer to approve */
    List<Payment> findByStatus(Payment.PaymentStatus status);

    /** Find all payments by merchant and status */
    List<Payment> findByMerchantAndStatus(AuthUser merchant, Payment.PaymentStatus status);

    /** Find all payments by customer */
    List<Payment> findByCustomer(AuthUser customer);

    /** Find all payments by customer - newest first */
    List<Payment> findByCustomerOrderByCreatedAtDesc(AuthUser customer);

    /** Find all payments by merchant - newest first */
    List<Payment> findByMerchantOrderByCreatedAtDesc(AuthUser merchant);

    /** Find all pending payments - newest first */
    List<Payment> findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus status);
}