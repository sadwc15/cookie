package com.example.gym.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    long countByGymClass_IdAndStatus(Long classId, String status);

    Optional<Booking> findByGymClass_IdAndUser_IdAndStatus(Long classId, Long userId, String status);

    List<Booking> findByUser_IdOrderByCreatedAtDesc(Long userId);

    long countByStatus(String status);
}