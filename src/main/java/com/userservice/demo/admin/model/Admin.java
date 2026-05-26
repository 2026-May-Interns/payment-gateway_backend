package com.userservice.demo.admin.model;

import com.userservice.demo.auth.model.AuthUser;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Represents the admin account in the system.
 * Authentication is handled by AuthUser.
 * Only one admin account exists, created on app startup.
 */
@Data
@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference to the authentication user account */
    @OneToOne
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUser authUser;

    @Column(nullable = false)
    private String fullName;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}