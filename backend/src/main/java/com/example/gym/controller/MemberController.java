package com.example.gym.controller;

import com.example.gym.user.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final UserRepository userRepo;

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var u = userRepo.findById(uid).orElse(null);
        return ResponseEntity.ok(Map.of("user", u));
    }

    @PutMapping("/me")
    public ResponseEntity<?> update(HttpServletRequest request, @RequestBody UpdateReq req) {
        Long uid = (Long) request.getAttribute("uid");
        var u = userRepo.findById(uid).orElse(null);
        if (u == null) return ResponseEntity.badRequest().build();
        if (req.getNickname() != null) u.setNickname(req.getNickname());
        if (req.getAvatar() != null) u.setAvatar(req.getAvatar());
        if (req.getPhone() != null) u.setPhone(req.getPhone());
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("user", u));
    }

    @GetMapping
    public ResponseEntity<?> list() {
        var list = userRepo.findAll();
        return ResponseEntity.ok(Map.of("list", list));
    }

    @Data
    public static class UpdateReq {
        private String nickname;
        private String avatar;
        private String phone;
    }
}