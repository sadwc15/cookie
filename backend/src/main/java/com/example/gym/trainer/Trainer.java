package com.example.gym.trainer;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "trainers")
@Data
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String bio;
    private String avatar;
    @Column(name = "rate_per_hour")
    private Integer ratePerHour;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}