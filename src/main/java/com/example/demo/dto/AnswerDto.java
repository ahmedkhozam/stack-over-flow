package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private boolean accepted;
    private Long questionId;
    private Long authorId;
}
