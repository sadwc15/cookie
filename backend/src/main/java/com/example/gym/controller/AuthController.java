package com.example.gym.controller;

import com.example.gym.security.JwtUtils;
import com.example.gym.service.WechatService;
import com.example.gym.user.User;
import com.example.gym.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepo;
    private final WechatService wechatService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        if (req.getCode() == null || req.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "code required"));
        }
        String openid = wechatService.code2Openid(req.getCode());
        var user = userRepo.findByOpenid(openid).orElseGet(() -> {
            var u = new User();
            u.setOpenid(openid);
            u.setNickname(req.getNickname());
            u.setAvatar(req.getAvatar());
            return userRepo.save(u);
        });
        if (req.getNickname() != null && !req.getNickname().isBlank()) user.setNickname(req.getNickname());
        if (req.getAvatar() != null && !req.getAvatar().isBlank()) user.setAvatar(req.getAvatar());
        userRepo.save(user);

        String token = jwtUtils.sign(user.getId(), openid);
        return ResponseEntity.ok(Map.of("token", token, "user", user));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var user = userRepo.findById(uid).orElse(null);
        return ResponseEntity.ok(Map.of("user", user));
    }

    @PostMapping("/elevate")
    public ResponseEntity<?> elevate(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        var user = userRepo.findById(uid).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body(Map.of("error", "not found"));
        user.setRole("admin");
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("user", user));
    }

    @Data
    public static class LoginReq {
        private String code;
        private String nickname;
        private String avatar;
    }
}