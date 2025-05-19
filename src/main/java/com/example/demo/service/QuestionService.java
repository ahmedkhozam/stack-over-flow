package com.example.demo.service;


import com.example.demo.dto.QuestionDto;

import java.util.List;

public interface QuestionService {
    QuestionDto createQuestion(QuestionDto questionDto);
    QuestionDto getQuestionById(Long id);
    List<QuestionDto> getAllQuestions();
    QuestionDto updateQuestion(Long id, QuestionDto dto);
    void deleteQuestion(Long id);

}
