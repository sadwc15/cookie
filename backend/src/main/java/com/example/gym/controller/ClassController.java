package com.example.gym.controller;

import com.example.gym.booking.Booking;
import com.example.gym.booking.BookingRepository;
import com.example.gym.classs.GymClass;
import com.example.gym.classs.GymClassRepository;
import com.example.gym.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassController {
    private final GymClassRepository classRepo;
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;

    @GetMapping
    public ResponseEntity<?> list() {
        var list = classRepo.findAll();
        return ResponseEntity.ok(Map.of("list", list));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody GymClass req) {
        if (req.getTitle() == null || req.getStartTime() == null || req.getEndTime() == null)
            return ResponseEntity.badRequest().body(Map.of("error", "title/start/end required"));
        classRepo.save(req);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/{id}/book")
    public ResponseEntity<?> book(@PathVariable Long id, HttpServletRequest request) {
        var cls = classRepo.findById(id).orElse(null);
        if (cls == null) return ResponseEntity.status(404).body(Map.of("error", "class not found"));
        Long uid = (Long) request.getAttribute("uid");
        if (bookingRepo.findByGymClass_IdAndUser_IdAndStatus(id, uid, "booked").isPresent())
            return ResponseEntity.badRequest().body(Map.of("error", "already booked"));
        long count = bookingRepo.countByGymClass_IdAndStatus(id, "booked");
        if (count >= cls.getCapacity()) return ResponseEntity.badRequest().body(Map.of("error", "class full"));
        var user = userRepo.findById(uid).orElseThrow();
        var b = new Booking();
        b.setGymClass(cls);
        b.setUser(user);
        b.setStatus("booked");
        bookingRepo.save(b);
        return ResponseEntity.ok(Map.of("ok", true, "price", cls.getPrice(), "class_id", id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var b = bookingRepo.findByGymClass_IdAndUser_IdAndStatus(id, uid, "booked").orElse(null);
        if (b != null) {
            b.setStatus("cancelled");
            bookingRepo.save(b);
        }
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/mine")
    public ResponseEntity<?> mine(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var list = bookingRepo.findByUser_IdOrderByCreatedAtDesc(uid);
        record Row(Long id, Long class_id, String status, String title, Object start_time, Object end_time,
                   Integer price) {
        }
        var resp = list.stream().map(b -> new Row(
                b.getId(),
                b.getGymClass().getId(),
                b.getStatus(),
                b.getGymClass().getTitle(),
                b.getGymClass().getStartTime(),
                b.getGymClass().getEndTime(),
                b.getGymClass().getPrice()
        )).toList();
        return ResponseEntity.ok(Map.of("list", resp));
    }
}