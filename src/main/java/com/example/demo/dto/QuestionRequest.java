package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionRequest {
    private String title;
    private String content;
    private List<String> tags; //  أسماء الوسوم كـ String
    private Integer bountyAmount;
    private LocalDateTime bountyExpiry;

}
