package com.example.demo.service;


import com.example.demo.dto.AnswerDto;

import java.util.List;

public interface AnswerService {
    AnswerDto addAnswer(Long questionId, AnswerDto dto);
    List<AnswerDto> getAnswersByQuestionId(Long questionId);
    AnswerDto updateAnswer(Long id, AnswerDto dto);
    void deleteAnswer(Long id);
    void acceptAnswer(Long answerId);


}
