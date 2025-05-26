package com.example.demo.dto;

import lombok.Data;

@Data
public class ReportRequest {
    private String reason;
    private Long questionId; // optional
    private Long answerId;   // optional
    private Long commentId;  // optional
}
