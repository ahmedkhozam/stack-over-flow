package com.example.demo.repository;

import com.example.demo.entity.Bounty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BountyRepository extends JpaRepository<Bounty, Long> {
    // ممكن تضيف استعلامات مخصصة هنا لو حبيت
}
