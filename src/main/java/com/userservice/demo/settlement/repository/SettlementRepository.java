package com.userservice.demo.settlement.repository;

import com.userservice.demo.auth.model.AuthUser;
import com.userservice.demo.settlement.model.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for Settlement database operations.
 */
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    /** Find all settlements for a merchant */
    List<Settlement> findByMerchant(AuthUser merchant);

    /** Find latest settlement */
    Settlement findTopByOrderBySettledAtDesc();
}