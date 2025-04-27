package com.example.demo.repository;

import com.example.demo.entity.StackUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StackUserRepository extends JpaRepository<StackUser, Long> {

    Optional<StackUser> findByEmail(String email);

    Optional<StackUser> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
