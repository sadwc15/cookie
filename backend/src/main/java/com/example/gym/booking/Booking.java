package com.example.gym.booking;

import com.example.gym.classs.GymClass;
import com.example.gym.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private GymClass gymClass;

    private String status = "booked";

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}