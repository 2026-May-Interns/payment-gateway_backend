package com.userservice.demo.user.service;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.auth.repository.AuthUserRepository;
import com.userservice.demo.exception.BadRequestException;
import com.userservice.demo.exception.DuplicateResourceException;
import com.userservice.demo.exception.ResourceNotFoundException;
import com.userservice.demo.otp.model.OtpRecord;
import com.userservice.demo.otp.service.OtpService;
import com.userservice.demo.security.JwtUtil;
import com.userservice.demo.user.dto.AuthResponse;
import com.userservice.demo.user.dto.LoginRequest;
import com.userservice.demo.user.dto.RegisterRequest;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterCustomerSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Maxwell Mainge");
        request.setEmail("max@gmail.com");
        request.setPhoneNumber("0712345678");
        request.setPassword("Password123");
        request.setNationalId("12345678");
        request.setKraPin("A123456789B");
        request.setDateOfBirth(LocalDate.of(2000, 1, 15));
        request.setTermsAccepted(true);

        AuthUser authUser = new AuthUser();
        authUser.setEmail("max@gmail.com");
        authUser.setRole(AuthUser.Role.CUSTOMER);

        when(authUserRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(customerRepository.existsByNationalId(any())).thenReturn(false);
        when(customerRepository.existsByKraPin(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(authUser);
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer result = userService.registerCustomer(request);

        assertNotNull(result);
        assertEquals("Maxwell Mainge", result.getFullName());
        assertEquals(Customer.AccountStatus.PENDING, result.getAccountStatus());
        verify(authUserRepository, times(1)).save(any(AuthUser.class));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("max@gmail.com");

        when(authUserRepository.existsByEmail("max@gmail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            userService.registerCustomer(request);
        });
    }

    @Test
    void shouldThrowExceptionWhenTermsNotAccepted() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("max@gmail.com");
        request.setPhoneNumber("0712345678");
        request.setNationalId("12345678");
        request.setKraPin("A123456789B");
        request.setTermsAccepted(false);

        when(authUserRepository.existsByEmail(any())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(any())).thenReturn(false);
        when(customerRepository.existsByNationalId(any())).thenReturn(false);
        when(customerRepository.existsByKraPin(any())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            userService.registerCustomer(request);
        });
    }

    @Test
    void shouldLoginCustomerSuccessfully() {
        LoginRequest request = new LoginRequest();
        request.setEmail("max@gmail.com");
        request.setPassword("Password123");

        AuthUser authUser = new AuthUser();
        authUser.setEmail("max@gmail.com");
        authUser.setPassword("encodedPassword");
        authUser.setRole(AuthUser.Role.CUSTOMER);

        Customer customer = new Customer();
        customer.setFullName("Maxwell Mainge");
        customer.setAccountStatus(Customer.AccountStatus.ACTIVE);
        customer.setFailedLoginAttempts(0);

        when(authUserRepository.findByEmail("max@gmail.com")).thenReturn(Optional.of(authUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);
        when(customerRepository.findByAuthUser(authUser)).thenReturn(Optional.of(customer));
        when(jwtUtil.generateAccessToken(any(), any())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refreshToken");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@gmail.com");
        request.setPassword("Password123");

        when(authUserRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.login(request);
        });
    }

    @Test
    void shouldThrowExceptionWhenAccountSuspended() {
        LoginRequest request = new LoginRequest();
        request.setEmail("max@gmail.com");
        request.setPassword("Password123");

        AuthUser authUser = new AuthUser();
        authUser.setEmail("max@gmail.com");
        authUser.setPassword("encodedPassword");
        authUser.setRole(AuthUser.Role.CUSTOMER);

        Customer customer = new Customer();
        customer.setAccountStatus(Customer.AccountStatus.SUSPENDED);

        when(authUserRepository.findByEmail("max@gmail.com")).thenReturn(Optional.of(authUser));
        when(passwordEncoder.matches("Password123", "encodedPassword")).thenReturn(true);
        when(customerRepository.findByAuthUser(authUser)).thenReturn(Optional.of(customer));

        assertThrows(BadRequestException.class, () -> {
            userService.login(request);
        });
    }
}