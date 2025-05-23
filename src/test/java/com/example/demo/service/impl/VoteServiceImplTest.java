package com.example.demo.service.impl;

import com.example.demo.entity.Answer;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.entity.Vote;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteServiceImplTest {

    @InjectMocks
    private VoteServiceImpl voteService;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private StackUserRepository userRepository;

    @Test
    void shouldUpvoteQuestionSuccessfully() {
        StackUser user = StackUser.builder().id(1L).email("user@test.com").reputation(0).build();
        StackUser questionOwner = StackUser.builder().id(2L).email("owner@test.com").reputation(0).build();
        Question question = Question.builder().id(10L).author(questionOwner).build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(questionRepository.findById(10L)).thenReturn(Optional.of(question));
        when(voteRepository.findByVoterIdAndQuestionId(1L, 10L)).thenReturn(Optional.empty());

        // ✅ mock static method
        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@test.com");

            voteService.voteOnQuestion(10L, 1); // upvote

            ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
            verify(voteRepository).save(voteCaptor.capture());
            Vote savedVote = voteCaptor.getValue();

            assertThat(savedVote.getValue()).isEqualTo(1);
            assertThat(savedVote.getQuestion().getId()).isEqualTo(10L);
            assertThat(savedVote.getVoter().getId()).isEqualTo(1L);
            assertThat(questionOwner.getReputation()).isEqualTo(10);
        }
    }

    @Test
    void shouldUpvoteAnswerSuccessfully() {
        // Arrange
        StackUser currentUser = StackUser.builder().id(1L).email("user@test.com").reputation(0).build();
        StackUser answerOwner = StackUser.builder().id(2L).email("answer@test.com").reputation(0).build();

        Answer answer = Answer.builder()
                .id(100L)
                .content("answer content")
                .author(answerOwner)
                .question(Question.builder().id(10L).build())
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(currentUser));
        when(answerRepository.findById(100L)).thenReturn(Optional.of(answer));
        when(voteRepository.findByVoterIdAndAnswerId(1L, 100L)).thenReturn(Optional.empty());

        // Act
        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@test.com");

            voteService.voteOnAnswer(100L, 1); // upvote
        }

        // Assert
        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository).save(voteCaptor.capture());

        Vote savedVote = voteCaptor.getValue();
        assertThat(savedVote.getValue()).isEqualTo(1);
        assertThat(savedVote.getAnswer().getId()).isEqualTo(100L);
        assertThat(savedVote.getVoter().getId()).isEqualTo(1L);
        assertThat(answerOwner.getReputation()).isEqualTo(10);
    }

    @Test
    void shouldChangeVoteValueOnAnswer() {
        StackUser currentUser = StackUser.builder().id(1L).email("user@test.com").reputation(0).build();
        StackUser answerOwner = StackUser.builder().id(2L).email("answer@test.com").reputation(0).build();

        Answer answer = Answer.builder()
                .id(200L)
                .content("answer")
                .author(answerOwner)
                .question(Question.builder().id(10L).build())
                .build();

        Vote existingVote = Vote.builder()
                .id(5L)
                .voter(currentUser)
                .answer(answer)
                .value(1)
                .build();

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(currentUser));
        when(answerRepository.findById(200L)).thenReturn(Optional.of(answer));
        when(voteRepository.findByVoterIdAndAnswerId(1L, 200L)).thenReturn(Optional.of(existingVote));

        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@test.com");

            voteService.voteOnAnswer(200L, -1); // change vote
        }

        assertThat(existingVote.getValue()).isEqualTo(-1);
    }

}
