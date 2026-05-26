package com.userservice.demo.auth.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Central authentication entity implementing Spring Security UserDetails.
 * Handles authentication for all user types - Customer, Merchant and Admin.
 * Business specific data is stored in respective business models.
 */
@Data
@Entity
@Table(name = "auth_users")
public class AuthUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique email used for authentication */
    @Column(nullable = false, unique = true)
    private String email;


    /** Encrypted password */
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    /** Role determines access level - CUSTOMER, MERCHANT or ADMIN */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Whether the account is enabled */
    private boolean enabled = true;

    /** Timestamp when auth account was created */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Role {
        CUSTOMER, MERCHANT, ADMIN
    }
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}