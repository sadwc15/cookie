package com.example.gym.order;

import com.example.gym.coupon.UserCoupon;
import com.example.gym.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true)
    private String orderNo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String subject;
    private Integer amount;
    private String status = "unpaid";

    @ManyToOne
    @JoinColumn(name = "coupon_user_id")
    private UserCoupon couponUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(columnDefinition = "JSON")
    private String extra;
}