package com.userservice.demo.wallet.repository;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for Wallet database operations.
 */
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /** Find wallet by auth user */
    Optional<Wallet> findByAuthUser(AuthUser authUser);

    /** Find wallet by auth user email */
    Optional<Wallet> findByAuthUser_Email(String email);

    /** Check if wallet exists for auth user */
    boolean existsByAuthUser(AuthUser authUser);
}