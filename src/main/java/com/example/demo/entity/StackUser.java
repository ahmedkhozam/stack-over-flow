package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stack_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StackUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String bio;

    @Column(nullable = false)
    private int reputation = 0;

}
