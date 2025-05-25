package com.example.demo.service.impl;


import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.BadgeService;
import com.example.demo.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final StackUserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final BadgeService badgeService;
    private final CommentRepository commentRepository;

    @Override
    public void voteOnQuestion(Long questionId, int value) {
        String email = SecurityUtil.getCurrentUserEmail();
        StackUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        StackUser questionOwner = question.getAuthor();
        Optional<Vote> existingVote = voteRepository.findByVoterIdAndQuestionId(user.getId(), questionId);

        existingVote.ifPresentOrElse(
                vote -> {
                    int oldValue = vote.getValue();
                    vote.setValue(value);
                    voteRepository.save(vote);
                    adjustReputation(questionOwner, oldValue, value);
                },
                () -> {
                    voteRepository.save(Vote.builder()
                            .value(value)
                            .voter(user)
                            .question(question)
                            .build());
                    adjustReputation(questionOwner, 0, value);
                }
        );

    }

    @Override
    public void voteOnAnswer(Long answerId, int value) {
        String email = SecurityUtil.getCurrentUserEmail();
        StackUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        StackUser answerOwner = answer.getAuthor();

        voteRepository.findByVoterIdAndAnswerId(user.getId(), answerId).ifPresentOrElse(
                existingVote -> {
                    int oldValue = existingVote.getValue();
                    existingVote.setValue(value);
                    voteRepository.save(existingVote);
                    adjustReputation(answerOwner, oldValue, value);
                },
                () -> {
                    Vote vote = Vote.builder()
                            .value(value)
                            .voter(user)
                            .answer(answer)
                            .build();
                    voteRepository.save(vote);
                    adjustReputation(answerOwner, 0, value); //  أول مرة يصوت
                }
        );

        badgeService.checkAndAssignBadgesForAnswer(answer.getAuthor(), answer);
    }

    private void adjustReputation(StackUser targetUser, int oldValue, int newValue) {
        int delta = calcReputationChange(oldValue, newValue);
        targetUser.setReputation(targetUser.getReputation() + delta);
        userRepository.save(targetUser);
    }

    private int calcReputationChange(int oldValue, int newValue) {
        // مثال: +1 ➜ +10، -1 ➜ -2
        return (toPoints(newValue) - toPoints(oldValue));
    }

    private int toPoints(int value) {
        return switch (value) {
            case 1 -> 10;
            case -1 -> -2;
            default -> 0;
        };
    }

    @Override
    public void voteOnComment(Long commentId, int value) {
        String email = SecurityUtil.getCurrentUserEmail();
        StackUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        voteRepository.findByVoterIdAndCommentId(user.getId(), commentId).ifPresentOrElse(
                existingVote -> {
                    existingVote.setValue(value);
                    voteRepository.save(existingVote);
                },
                () -> {
                    Vote vote = Vote.builder()
                            .voter(user)
                            .value(value)
                            .comment(comment)
                            .build();
                    voteRepository.save(vote);
                }
        );
    }


}
