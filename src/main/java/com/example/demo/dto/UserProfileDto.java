package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private int reputation;

    private int questionCount;
    private int answerCount;
    private int commentCount;

    private List<QuestionDto> questions;
    private List<AnswerDto> answers;
}
