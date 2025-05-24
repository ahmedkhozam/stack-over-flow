package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // مثل "Nice Answer", "Good Question"
    private String description; // وصف الشارة
    private String level; // BRONZE, SILVER, GOLD

    @ManyToMany(mappedBy = "badges")
    private Set<StackUser> users = new HashSet<>();
}
