package com.example.gym.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String openid;

    private String nickname;
    private String avatar;
    private String phone;
    private String role = "member";
    private Integer level = 1;
    private Integer points = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}