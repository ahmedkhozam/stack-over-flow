package com.example.demo.service.impl;


import com.example.demo.dto.AnswerDto;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnswerServiceImplTest {

    @InjectMocks
    private AnswerServiceImpl answerService;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private StackUserRepository userRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddAnswerToQuestionSuccessfully() {
        StackUser user = StackUser.builder().id(1L).email("user@test.com").build();
        Question question = Question.builder().id(1L).title("Q").author(user).build();

        AnswerDto dto = new AnswerDto();
        dto.setContent("My answer");
        dto.setAuthorId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Answer savedAnswer = Answer.builder()
                .id(10L)
                .content("My answer")
                .author(user)
                .question(question)
                .createdAt(LocalDateTime.now())
                .build();

        when(answerRepository.save(any(Answer.class))).thenReturn(savedAnswer);

        AnswerDto result = answerService.addAnswer(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getContent()).isEqualTo("My answer");
    }
    @Test
    void shouldThrowWhenQuestionNotFoundInAddAnswer() {
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        AnswerDto dto = new AnswerDto();
        dto.setAuthorId(1L);
        dto.setContent("Any");

        assertThatThrownBy(() -> answerService.addAnswer(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Question not found");
    }
    @Test
    void shouldThrowWhenUserNotFoundInAddAnswer() {
        Question q = Question.builder().id(1L).title("Test").build();
        when(questionRepository.findById(1L)).thenReturn(Optional.of(q));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        AnswerDto dto = new AnswerDto();
        dto.setAuthorId(1L);
        dto.setContent("Content");

        assertThatThrownBy(() -> answerService.addAnswer(1L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldUpdateAnswerSuccessfully() {
        StackUser user = StackUser.builder().id(1L).email("user@test.com").build();
        Answer existingAnswer = Answer.builder().id(5L).content("Old").author(user).question(new Question()).build();

        when(answerRepository.findById(5L)).thenReturn(Optional.of(existingAnswer));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(answerRepository.save(any())).thenReturn(existingAnswer);
        when(voteRepository.countByAnswerIdAndValue(anyLong(), anyInt())).thenReturn(0);

        AnswerDto dto = new AnswerDto();
        dto.setContent("Updated");

        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@test.com");

            AnswerDto result = answerService.updateAnswer(5L, dto);

            assertThat(result.getContent()).isEqualTo("Updated");
        }
    }

    @Test
    void shouldThrowWhenNotOwnerInUpdate() {
        StackUser owner = StackUser.builder().id(1L).email("owner@test.com").build();
        StackUser other = StackUser.builder().id(2L).email("other@test.com").build();
        Answer answer = Answer.builder().id(5L).content("Old").author(owner).question(new Question()).build();

        when(answerRepository.findById(5L)).thenReturn(Optional.of(answer));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));

        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("other@test.com");

            AnswerDto dto = new AnswerDto();
            dto.setContent("Edited by someone else");

            assertThatThrownBy(() -> answerService.updateAnswer(5L, dto))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("You are not allowed to modify this answer");
        }
    }

    @Test
    void shouldDeleteAnswerSuccessfully() {
        StackUser user = StackUser.builder().id(1L).email("user@test.com").build();
        Question question = Question.builder().id(1L).build();
        Answer answer = Answer.builder().id(3L).author(user).question(question).build();

        when(answerRepository.findById(3L)).thenReturn(Optional.of(answer));
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@test.com");

            answerService.deleteAnswer(3L);
            verify(answerRepository).delete(answer);
        }
    }

    @Test
    void shouldThrowWhenNotOwnerInDelete() {
        StackUser owner = StackUser.builder().id(1L).email("owner@test.com").build();
        StackUser other = StackUser.builder().id(2L).email("other@test.com").build();

        Question question = Question.builder().id(1L).build();
        Answer answer = Answer.builder().id(3L).author(owner).question(question).build();

        when(answerRepository.findById(3L)).thenReturn(Optional.of(answer));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(other));

        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("other@test.com");

            assertThatThrownBy(() -> answerService.deleteAnswer(3L))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("You are not allowed to delete this answer");
        }
    }
    @Test
    void shouldReturnAnswersByQuestionId() {
        StackUser user = StackUser.builder().id(1L).email("a@b.com").build();
        Question question = Question.builder().id(1L).build();
        Answer answer1 = Answer.builder().id(1L).content("Answer 1").question(question).author(user).build();
        Answer answer2 = Answer.builder().id(2L).content("Answer 2").question(question).author(user).build();

        when(answerRepository.findByQuestionId(1L)).thenReturn(List.of(answer1, answer2));
        when(voteRepository.countByAnswerIdAndValue(anyLong(), eq(1))).thenReturn(0);
        when(voteRepository.countByAnswerIdAndValue(anyLong(), eq(-1))).thenReturn(0);

        List<AnswerDto> result = answerService.getAnswersByQuestionId(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("Answer 1");
    }


}
