package com.example.demo.service.impl;


import com.example.demo.entity.Answer;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.entity.Vote;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final StackUserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;


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
                    adjustReputation(answerOwner, oldValue, value); // ✅ التعديل هنا
                },
                () -> {
                    Vote vote = Vote.builder()
                            .value(value)
                            .voter(user)
                            .answer(answer)
                            .build();
                    voteRepository.save(vote);
                    adjustReputation(answerOwner, 0, value); // ✅ أول مرة يصوت
                }
        );
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

}
