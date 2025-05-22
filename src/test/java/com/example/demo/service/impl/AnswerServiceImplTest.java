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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

}
