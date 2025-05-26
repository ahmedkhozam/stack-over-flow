package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportResponse {
    private Long id;
    private String reason;
    private String reporterEmail;
    private LocalDateTime reportedAt;
    private Long questionId;
    private Long answerId;
    private Long commentId;
}
