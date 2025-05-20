package com.example.demo.service.impl;


import com.example.demo.dto.QuestionDto;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final StackUserRepository userRepository;

    @Override
    public QuestionDto createQuestion(QuestionDto dto) {
        StackUser author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Question question = Question.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .author(author)
                .build();

        return mapToDto(questionRepository.save(question));
    }

    @Override
    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        return mapToDto(question);
    }

    @Override
    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private QuestionDto mapToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        BeanUtils.copyProperties(question, dto);
        dto.setAuthorId(question.getAuthor().getId());
        return dto;
    }

    @Override
    public QuestionDto updateQuestion(Long id, QuestionDto dto) {
        String currentEmail = SecurityUtil.getCurrentUserEmail();


        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!question.getAuthor().getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("You are not allowed to modify this question");
        }

        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());

        return mapToDto(questionRepository.save(question));
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        String currentEmail = SecurityUtil.getCurrentUserEmail();

        if (!question.getAuthor().getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("You are not allowed to modify this question");
        }

        questionRepository.delete(question);
    }

}
