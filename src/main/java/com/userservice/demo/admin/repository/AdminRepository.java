package com.userservice.demo.admin.repository;

import com.userservice.demo.admin.model.Admin;
import com.userservice.demo.auth.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for Admin database operations.
 */
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /** Find admin by their auth user */
    Optional<Admin> findByAuthUser(AuthUser authUser);

    /** Find admin by email through auth user relationship */
    Optional<Admin> findByAuthUser_Email(String email);

    /** Check if admin exists by email */
    boolean existsByAuthUser_Email(String email);
}