package com.userservice.demo.settlement.controller;

import com.userservice.demo.settlement.model.Settlement;
import com.userservice.demo.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for settlement endpoints.
 * Admin can trigger manual settlement.
 * Merchants can view their settlement history.
 */
@RestController
@RequestMapping("/api/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    /**
     * Admin triggers manual settlement.
     */
    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> runManualSettlement() {
        settlementService.runManualSettlement();
        return ResponseEntity.ok("Settlement completed successfully");
    }

    /**
     * Merchant views their settlement history.
     */
    @GetMapping("/merchant")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<List<Settlement>> getMerchantSettlements(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(settlementService.getMerchantSettlements(email));
    }

    /**
     * Admin views last settlement run.
     */
    @GetMapping("/last")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Settlement> getLastSettlement() {
        return ResponseEntity.ok(settlementService.getLastSettlement());
    }
}