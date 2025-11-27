package com.example.gym.coupon;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String title;
    private String type;   // amount | percent
    private Integer value;
    @Column(name = "min_spend")
    private Integer minSpend;
    private Integer total;
    private Integer claimed;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;
    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}