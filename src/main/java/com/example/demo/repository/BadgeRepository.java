package com.example.demo.repository;

import com.example.demo.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    
    Optional<Badge> findByName(String name); // مفيد وقت منح الشارات بالاسم
}
