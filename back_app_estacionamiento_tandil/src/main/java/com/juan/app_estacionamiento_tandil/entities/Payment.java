package com.juan.app_estacionamiento_tandil.entities;

import com.juan.app_estacionamiento_tandil.entities.enums.PaymentState;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentState state;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Payment() {}
}
