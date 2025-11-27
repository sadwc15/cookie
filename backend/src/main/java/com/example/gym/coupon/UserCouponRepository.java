package com.example.gym.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Optional<UserCoupon> findByCoupon_IdAndUser_Id(Long couponId, Long userId);

    List<UserCoupon> findByUser_IdOrderByIdDesc(Long userId);
}