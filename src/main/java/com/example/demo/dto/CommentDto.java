package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long authorId;
    private Long questionId; // optional
    private Long answerId;   // optional
}
