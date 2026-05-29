package com.userservice.demo.auth.repository;

import com.userservice.demo.auth.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;import java.util.List;

/**
 * Repository for AuthUser database operations.
 * Used by Spring Security to load users during authentication.
 */
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    /** Find auth user by email - used during login */
    Optional<AuthUser> findByEmail(String email);

    /** Find all users by role */
    List<AuthUser> findByRole(AuthUser.Role role);

    /** Check if email already exists */
    boolean existsByEmail(String email);
}