package com.example.gym.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 查询当前仍在有效期、未领完的优惠券（按照 id 倒序）
    @Query("select c from Coupon c " +
            "where :now between c.validFrom and c.validTo " +
            "and c.claimed < c.total order by c.id desc")
    List<Coupon> findAvailable(LocalDateTime now);

    // 根据优惠码查找
    Optional<Coupon> findByCode(String code);

    // 可选：统计某个时间点仍有效的优惠券数量
    @Query("select count(c) from Coupon c " +
            "where :now between c.validFrom and c.validTo " +
            "and c.claimed < c.total")
    long countAvailable(LocalDateTime now);
}