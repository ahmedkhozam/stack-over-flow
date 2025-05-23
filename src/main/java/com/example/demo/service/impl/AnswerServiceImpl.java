package com.example.demo.service.impl;


import com.example.demo.dto.AnswerDto;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.AnswerService;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final StackUserRepository userRepository;
    private final VoteRepository voteRepository;
    private final NotificationService notificationService;



    @Override
    public AnswerDto addAnswer(Long questionId, AnswerDto dto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        StackUser author = userRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Answer answer = Answer.builder()
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .accepted(false)
                .question(question)
                .author(author)
                .build();
        Answer saved = answerRepository.save(answer);

        notificationService.notifyUser(
                question.getAuthor().getId(),
                author.getUsername() + " أجاب على سؤالك",
                question.getId(),
                null
        );

        return mapToDto(saved);

    }

    @Override
    public List<AnswerDto> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    private AnswerDto mapToDto(Answer answer) {
        AnswerDto dto = new AnswerDto();
        BeanUtils.copyProperties(answer, dto);
        dto.setAuthorId(answer.getAuthor().getId());
        dto.setQuestionId(answer.getQuestion().getId());

        dto.setUpvotes(voteRepository.countByAnswerIdAndValue(answer.getId(), 1));
        dto.setDownvotes(voteRepository.countByAnswerIdAndValue(answer.getId(), -1));

        return dto;
    }

    @Override
    public AnswerDto updateAnswer(Long id, AnswerDto dto) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        String currentEmail = SecurityUtil.getCurrentUserEmail();

        if (!answer.getAuthor().getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("You are not allowed to modify this answer");
        }

        answer.setContent(dto.getContent());
        return mapToDto(answerRepository.save(answer));
    }

    @Override
    public void deleteAnswer(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
        String currentEmail = SecurityUtil.getCurrentUserEmail();

        if (!answer.getAuthor().getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("You are not allowed to modify this answer");
        }

        answerRepository.delete(answer);
    }

    @Override
    public void acceptAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        StackUser currentUser = getCurrentUser();

        if (!answer.getQuestion().getAuthor().getEmail().equals(currentUser.getEmail())) {
            throw new AccessDeniedException("Only the question owner can accept an answer");
        }

        // لو فيه إجابة مقبولة قبل كده نشيل القبول منها
        answerRepository.findByQuestionIdAndAcceptedTrue(answer.getQuestion().getId())
                .ifPresent(existing -> {
                    existing.setAccepted(false);
                    answerRepository.save(existing);
                });

        answer.setAccepted(true);
        answerRepository.save(answer);

        // نضيف 15 نقطة لصاحب الإجابة
        StackUser answerOwner = answer.getAuthor();
        answerOwner.setReputation(answerOwner.getReputation() + 15);
        userRepository.save(answerOwner);
    }

    private StackUser getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }


}
