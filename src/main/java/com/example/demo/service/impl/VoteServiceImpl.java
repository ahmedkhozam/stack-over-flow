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

        voteRepository.findByVoterIdAndQuestionId(user.getId(), questionId).ifPresentOrElse(
            existingVote -> {
                existingVote.setValue(value); // تعديل التصويت
                voteRepository.save(existingVote);
            },
            () -> {
                Vote vote = Vote.builder()
                        .value(value)
                        .voter(user)
                        .question(question)
                        .build();
                voteRepository.save(vote);
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

        voteRepository.findByVoterIdAndAnswerId(user.getId(), answerId).ifPresentOrElse(
            existingVote -> {
                existingVote.setValue(value); // تعديل التصويت
                voteRepository.save(existingVote);
            },
            () -> {
                Vote vote = Vote.builder()
                        .value(value)
                        .voter(user)
                        .answer(answer)
                        .build();
                voteRepository.save(vote);
            }
        );
    }
}
