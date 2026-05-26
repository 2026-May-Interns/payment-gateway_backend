package com.userservice.demo.admin.controller;

import com.userservice.demo.admin.service.AdminService;
import com.userservice.demo.user.model.Customer;
import com.userservice.demo.user.model.Merchant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;import org.springframework.data.domain.Page;

/**
 * Controller for admin operations.
 * All endpoints restricted to ADMIN role only.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /** Get all customers with pagination */
    @GetMapping("/customers")
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllCustomers(page, size));
    }

    /** Get customer by ID */
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCustomerById(id));
    }

    /** Suspend a customer account */
    @PutMapping("/customers/{id}/suspend")
    public ResponseEntity<String> suspendCustomer(@PathVariable Long id) {
        adminService.suspendCustomer(id);
        return ResponseEntity.ok("Customer suspended successfully");
    }

    /** Unsuspend a customer account */
    @PutMapping("/customers/{id}/unsuspend")
    public ResponseEntity<String> unsuspendCustomer(@PathVariable Long id) {
        adminService.unsuspendCustomer(id);
        return ResponseEntity.ok("Customer unsuspended successfully");
    }

    /** Unlock a locked customer account */
    @PutMapping("/customers/{id}/unlock")
    public ResponseEntity<String> unlockCustomer(@PathVariable Long id) {
        adminService.unlockCustomer(id);
        return ResponseEntity.ok("Customer account unlocked successfully");
    }

    /** Delete a customer account */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        adminService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully");
    }

    /** Get all merchants with pagination */
    @GetMapping("/merchants")
    public ResponseEntity<Page<Merchant>> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllMerchants(page, size));
    }

    /** Get merchant by ID */
    @GetMapping("/merchants/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getMerchantById(id));
    }

    /** Suspend a merchant account */
    @PutMapping("/merchants/{id}/suspend")
    public ResponseEntity<String> suspendMerchant(@PathVariable Long id) {
        adminService.suspendMerchant(id);
        return ResponseEntity.ok("Merchant suspended successfully");
    }

    /** Unsuspend a merchant account */
    @PutMapping("/merchants/{id}/unsuspend")
    public ResponseEntity<String> unsuspendMerchant(@PathVariable Long id) {
        adminService.unsuspendMerchant(id);
        return ResponseEntity.ok("Merchant unsuspended successfully");
    }

    /** Unlock a locked merchant account */
    @PutMapping("/merchants/{id}/unlock")
    public ResponseEntity<String> unlockMerchant(@PathVariable Long id) {
        adminService.unlockMerchant(id);
        return ResponseEntity.ok("Merchant account unlocked successfully");
    }

    /** Delete a merchant account */
    @DeleteMapping("/merchants/{id}")
    public ResponseEntity<String> deleteMerchant(@PathVariable Long id) {
        adminService.deleteMerchant(id);
        return ResponseEntity.ok("Merchant deleted successfully");
    }
}