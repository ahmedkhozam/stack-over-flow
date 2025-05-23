package com.example.demo.service.impl;


import com.example.demo.dto.QuestionDto;
import com.example.demo.dto.QuestionRequest;
import com.example.demo.dto.QuestionResponse;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.entity.Tag;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.repository.TagRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final StackUserRepository userRepository;
    private final VoteRepository voteRepository;
    private final TagRepository tagRepository;


    @Override
    public QuestionResponse createQuestion(QuestionRequest request) {


        StackUser user = getCurrentUser();

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .author(user)
                .tags(extractTagsFromNames(request.getTags()))
                .build();

        return mapToDto(questionRepository.save(question));
    }

    @Override
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        return mapToDto(question);
    }

    @Override
    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    private QuestionResponse mapToDto(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .authorName(question.getAuthor().getUsername())
                .tags(
                        question.getTags().stream()
                                .map(Tag::getName)
                                .collect(Collectors.toList())
                )
                .upvotes(voteRepository.countByQuestionIdAndValue(question.getId(), 1))
                .downvotes(voteRepository.countByQuestionIdAndValue(question.getId(), -1))
                .build();
    }


    @Override
    public QuestionResponse updateQuestion(Long id, QuestionRequest dto) {

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

    private Set<Tag> extractTagsFromNames(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> Tag.builder().name(name).build());
            tags.add(tag);
        }
        return tags;
    }

    private StackUser getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

}
