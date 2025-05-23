package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean read = false;

    private LocalDateTime createdAt;

    @ManyToOne
    private StackUser recipient;

    @ManyToOne
    private Question question; // optional

    @ManyToOne
    private Answer answer; // optional
}
