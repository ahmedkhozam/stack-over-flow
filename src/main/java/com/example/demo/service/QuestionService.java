package com.example.demo.service;


import com.example.demo.dto.QuestionDto;
import com.example.demo.dto.QuestionRequest;
import com.example.demo.dto.QuestionResponse;

import java.util.List;

public interface QuestionService {
    QuestionResponse createQuestion(QuestionRequest request);
    QuestionResponse getQuestionById(Long id);
    List<QuestionResponse> getAllQuestions();
    QuestionResponse updateQuestion(Long id, QuestionRequest dto);
    void deleteQuestion(Long id);

}
