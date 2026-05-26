package com.userservice.demo.otp.service;

import com.userservice.demo.exception.BadRequestException;
import com.userservice.demo.otp.dto.VerifyOtpRequest;
import com.userservice.demo.otp.model.OtpRecord;
import com.userservice.demo.otp.repository.OtpRepository;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.repository.CustomerRepository;
import com.userservice.demo.user.repository.MerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private OtpService otpService;

    @Test
    void shouldGenerateOtpSuccessfully() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(otpRepository.save(any(OtpRecord.class))).thenAnswer(i -> i.getArgument(0));

        String otp = otpService.generateOtp("max@gmail.com", OtpRecord.OtpType.EMAIL);

        assertNotNull(otp);
        assertEquals(6, otp.length());
        verify(otpRepository, times(1)).save(any(OtpRecord.class));
    }

    @Test
    void shouldVerifyEmailOtpAndSetEmailVerified() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setRecipient("max@gmail.com");
        request.setOtpType("EMAIL");
        request.setOtpCode("123456");

        Customer customer = new Customer();
        customer.setEmailVerified(false);
        customer.setPhoneVerified(true);
        customer.setAccountStatus(Customer.AccountStatus.PENDING);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("otp:email:max@gmail.com")).thenReturn("123456");
        when(customerRepository.findByAuthUser_Email("max@gmail.com")).thenReturn(Optional.of(customer));
        when(otpRepository.findByRecipientAndOtpTypeAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());

        boolean result = otpService.verifyOtp(request);

        assertTrue(result);
        assertTrue(customer.isEmailVerified());
        assertEquals(Customer.AccountStatus.ACTIVE, customer.getAccountStatus());
    }

    @Test
    void shouldThrowExceptionWhenOtpExpired() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setRecipient("max@gmail.com");
        request.setOtpType("EMAIL");
        request.setOtpCode("123456");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("otp:email:max@gmail.com")).thenReturn(null);

        assertThrows(BadRequestException.class, () -> otpService.verifyOtp(request));
    }

    @Test
    void shouldThrowExceptionWhenOtpIsWrong() {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setRecipient("max@gmail.com");
        request.setOtpType("EMAIL");
        request.setOtpCode("000000");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("otp:email:max@gmail.com")).thenReturn("123456");

        assertThrows(BadRequestException.class, () -> otpService.verifyOtp(request));
    }
}