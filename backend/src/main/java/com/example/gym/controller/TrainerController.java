package com.example.gym.controller;

import com.example.gym.trainer.Trainer;
import com.example.gym.trainer.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
public class TrainerController {
    private final TrainerRepository repo;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(Map.of("list", repo.findAll()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Trainer req) {
        if (req.getName() == null || req.getName().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "name required"));
        repo.save(req);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Trainer req) {
        var t = repo.findById(id).orElse(null);
        if (t == null) return ResponseEntity.notFound().build();
        if (req.getName() != null) t.setName(req.getName());
        if (req.getBio() != null) t.setBio(req.getBio());
        if (req.getAvatar() != null) t.setAvatar(req.getAvatar());
        if (req.getRatePerHour() != null) t.setRatePerHour(req.getRatePerHour());
        repo.save(t);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("ok", true));
    }
}