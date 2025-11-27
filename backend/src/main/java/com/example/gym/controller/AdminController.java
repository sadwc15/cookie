package com.example.gym.controller;

import com.example.gym.booking.BookingRepository;
import com.example.gym.order.OrderRepository;
import com.example.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final BookingRepository bookingRepo;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        long totalUsers = userRepo.count();
        long totalOrders = orderRepo.count();
        int revenue = orderRepo.findAll().stream()
                .filter(o -> "paid".equals(o.getStatus()))
                .mapToInt(o -> o.getAmount() == null ? 0 : o.getAmount())
                .sum();
        long classBookings = bookingRepo.countByStatus("booked");
        return ResponseEntity.ok(Map.of(
                "total_users", totalUsers,
                "total_orders", totalOrders,
                "revenue", revenue,
                "today_orders", 0,
                "today_revenue", 0,
                "class_bookings", classBookings,
                "top_classes", java.util.List.of()
        ));
    }
}