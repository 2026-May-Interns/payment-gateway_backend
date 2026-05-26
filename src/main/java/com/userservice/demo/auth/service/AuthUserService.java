package com.userservice.demo.auth.service;

import com.userservice.demo.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security UserDetailsService implementation.
 * Loads user authentication details by email during login.
 * Used by Spring Security to authenticate users.
 */
@Service
@RequiredArgsConstructor
public class AuthUserService implements UserDetailsService {

    private final AuthUserRepository authUserRepository;

    /**
     * Loads user by email for Spring Security authentication.
     *
     * @param email the email to look up
     * @return UserDetails for the found user
     * @throws UsernameNotFoundException if no user found with given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}