package com.userservice.demo;

import com.userservice.demo.admin.model.Admin;
import com.userservice.demo.admin.repository.AdminRepository;
import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Main entry point for the Payment Gateway User Service.
 * Creates default admin account on startup if it doesn't exist.
 */
@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Creates the default admin account on app startup.
	 * Only runs if admin account doesn't already exist.
	 */
	@Bean
	public CommandLineRunner createDefaultAdmin(
			AuthUserRepository authUserRepository,
			AdminRepository adminRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (!authUserRepository.existsByEmail("admin@gateway.com")) {
				// Create auth user for admin
				AuthUser authUser = new AuthUser();
				authUser.setEmail("admin@gateway.com");
				authUser.setPassword(passwordEncoder.encode("Admin@1234"));
				authUser.setRole(AuthUser.Role.ADMIN);
				AuthUser savedAuthUser = authUserRepository.save(authUser);

				// Create admin profile
				Admin admin = new Admin();
				admin.setAuthUser(savedAuthUser);
				admin.setFullName("System Admin");
				adminRepository.save(admin);

				System.out.println("===========================================");
				System.out.println("Default admin account created");
				System.out.println("Email: admin@gateway.com");
				System.out.println("Password: Admin@1234");
				System.out.println("===========================================");
			}
		};
	}
}