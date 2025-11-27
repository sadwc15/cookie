package com.example.gym.controller;

import com.example.gym.coupon.CouponRepository;
import com.example.gym.coupon.UserCoupon;
import com.example.gym.coupon.UserCouponRepository;
import com.example.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponRepository couponRepo;
    private final UserCouponRepository ucRepo;
    private final UserRepository userRepo;

    @GetMapping("/available")
    public ResponseEntity<?> available() {
        var list = couponRepo.findAvailable(LocalDateTime.now());
        return ResponseEntity.ok(Map.of("list", list));
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<?> claim(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var c = couponRepo.findById(id).orElse(null);
        if (c == null) return ResponseEntity.status(404).body(Map.of("error", "coupon not found"));
        if (c.getClaimed() >= c.getTotal()) return ResponseEntity.badRequest().body(Map.of("error", "sold out"));
        if (ucRepo.findByCoupon_IdAndUser_Id(id, uid).isPresent())
            return ResponseEntity.badRequest().body(Map.of("error", "already claimed"));
        var u = userRepo.findById(uid).orElseThrow();
        var uc = new UserCoupon();
        uc.setCoupon(c);
        uc.setUser(u);
        uc.setStatus("unused");
        ucRepo.save(uc);
        c.setClaimed(c.getClaimed() + 1);
        couponRepo.save(c);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/mine")
    public ResponseEntity<?> mine(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var list = ucRepo.findByUser_IdOrderByIdDesc(uid);
        return ResponseEntity.ok(Map.of("list", list));
    }
}