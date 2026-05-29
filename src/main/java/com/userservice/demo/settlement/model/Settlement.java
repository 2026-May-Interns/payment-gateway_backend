package com.userservice.demo.settlement.model;

import com.userservice.demo.auth.model.AuthUser;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a settlement record.
 * Created when merchant pending balance is moved to available balance.
 */
@Data
@Entity
@Table(name = "settlements")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The merchant who received the settlement */
    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private AuthUser merchant;

    /** Amount settled */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /** When the settlement was processed */
    @Column(nullable = false, updatable = false)
    private LocalDateTime settledAt;

    @PrePersist
    protected void onCreate() {
        settledAt = LocalDateTime.now();
    }
}