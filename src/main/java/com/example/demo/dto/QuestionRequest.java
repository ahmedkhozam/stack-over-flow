package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {
    private String title;
    private String content;
    //private Long authorId;
    private List<String> tags; // ✅ أسماء الوسوم كـ String
}
