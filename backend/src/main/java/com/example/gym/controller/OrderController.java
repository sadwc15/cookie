package com.example.gym.controller;

import com.example.gym.classs.GymClassRepository;
import com.example.gym.coupon.UserCouponRepository;
import com.example.gym.order.Order;
import com.example.gym.order.OrderRepository;
import com.example.gym.service.PaymentService;
import com.example.gym.user.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderRepository orderRepo;
    private final UserCouponRepository ucRepo;
    private final UserRepository userRepo;
    private final GymClassRepository classRepo;
    private final PaymentService payService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateReq req, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        int amount = req.getAmount() != null ? req.getAmount() : 0;

        if (req.getClassId() != null) {
            var cls = classRepo.findById(req.getClassId()).orElse(null);
            if (cls == null) return ResponseEntity.status(404).body(Map.of("error", "class not found"));
            amount = cls.getPrice();
        }

        var couponUser = (req.getCouponUserId() != null) ? ucRepo.findById(req.getCouponUserId()).orElse(null) : null;
        if (couponUser != null) {
            var c = couponUser.getCoupon();
            var now = LocalDateTime.now();
            if (now.isBefore(c.getValidFrom()) || now.isAfter(c.getValidTo()))
                return ResponseEntity.badRequest().body(Map.of("error", "coupon expired"));
            if (amount < c.getMinSpend())
                return ResponseEntity.badRequest().body(Map.of("error", "amount below coupon min spend"));
            if ("amount".equals(c.getType())) amount = Math.max(0, amount - c.getValue());
            else if ("percent".equals(c.getType())) amount = (int) Math.floor(amount * (100 - c.getValue()) / 100.0);
        }

        var o = new Order();
        o.setOrderNo("O" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6));
        o.setUser(userRepo.findById(uid).orElseThrow());
        o.setSubject(req.getSubject() != null ? req.getSubject() : "Gym Order");
        o.setAmount(amount);
        o.setStatus("unpaid");
        o.setCouponUser(couponUser);
        o.setExtra("{\"class_id\":" + (req.getClassId() == null ? "null" : req.getClassId()) + "}");
        orderRepo.save(o);

        var pay = payService.createPaymentIntent(o);
        return ResponseEntity.ok(Map.of("order", o, "payment", pay));
    }

    @GetMapping("/mine")
    public ResponseEntity<?> mine(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var list = orderRepo.findByUser_IdOrderByIdDesc(uid);
        return ResponseEntity.ok(Map.of("list", list));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var o = orderRepo.findById(id).orElse(null);
        if (o == null || !o.getUser().getId().equals(uid))
            return ResponseEntity.status(404).body(Map.of("error", "order not found"));
        if ("paid".equals(o.getStatus())) return ResponseEntity.ok(Map.of("ok", true, "order", o));
        o.setStatus("paid");
        o.setPaidAt(LocalDateTime.now());
        orderRepo.save(o);
        if (o.getCouponUser() != null) {
            var cu = o.getCouponUser();
            cu.setStatus("used");
            cu.setUsedAt(LocalDateTime.now());
            ucRepo.save(cu);
        }
        return ResponseEntity.ok(Map.of("ok", true, "order", o));
    }

    @Data
    public static class CreateReq {
        private String subject;
        private Integer amount;
        private Long classId;
        private Long couponUserId;
    }
}