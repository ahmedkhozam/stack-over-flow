package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class QuestionResponse {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private List<String> tags; //  أسماء الوسوم
    private int upvotes;
    private int downvotes;
    private Integer bountyAmount;
    private LocalDateTime bountyExpiry;

}
