package com.example.demo.service.impl;


import com.example.demo.dto.CommentDto;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.CommentService;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final StackUserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final NotificationService notificationService;
    private final VoteRepository voteRepository;

    @Override
    public CommentDto addCommentToQuestion(Long questionId, CommentDto dto) {
        StackUser currentUser = getCurrentUser();
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .author(currentUser)
                .question(question)
                .build();

        Comment saved = commentRepository.save(comment);

        notificationService.notifyUser(
                question.getAuthor().getId(),
                currentUser.getUsername() + " علّق على سؤالك",
                question.getId(),
                null
        );

        return mapToDto(saved);

    }

    @Override
    public CommentDto addCommentToAnswer(Long answerId, CommentDto dto) {
        StackUser currentUser = getCurrentUser();
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .author(currentUser)
                .answer(answer)
                .build();

        Comment saved = commentRepository.save(comment);

        notificationService.notifyUser(
                answer.getAuthor().getId(),
                currentUser.getUsername() + " علّق على اجابتك",
                answer.getQuestion().getId(),
                answer.getId()
        );

        return mapToDto(saved);    }

    @Override
    public List<CommentDto> getCommentsForQuestion(Long questionId) {
        return commentRepository.findByQuestionId(questionId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsForAnswer(Long answerId) {
        return commentRepository.findByAnswerId(answerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private StackUser getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        BeanUtils.copyProperties(comment, dto);
        dto.setAuthorId(comment.getAuthor().getId());
        if (comment.getQuestion() != null)
            dto.setQuestionId(comment.getQuestion().getId());
        if (comment.getAnswer() != null)
            dto.setAnswerId(comment.getAnswer().getId());

        int upvotes = voteRepository.countByCommentIdAndValue(comment.getId(), 1);
        int downvotes = voteRepository.countByCommentIdAndValue(comment.getId(), -1);

        dto.setUpvotes(upvotes);
        dto.setDownvotes(downvotes);

        return dto;
    }
}
