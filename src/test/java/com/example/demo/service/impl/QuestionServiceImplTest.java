package com.example.demo.service.impl;

import com.example.demo.dto.QuestionDto;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceImplTest {

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private StackUserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateQuestionSuccessfully() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setAuthorId(1L);

        StackUser user = StackUser.builder()
                .id(1L)
                .username("ahmed")
                .email("ahmed@gmail.com")
                .build();

        Question savedQuestion = Question.builder()
                .id(10L)
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(questionRepository.save(any(Question.class))).thenReturn(savedQuestion);

        // Act
        QuestionDto result = questionService.createQuestion(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("Test Title");

        verify(userRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).save(any(Question.class));
    }
    @Test
    void shouldThrowWhenUserNotFoundWhileCreatingQuestion() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setTitle("Any");
        dto.setContent("Any");
        dto.setAuthorId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> questionService.createQuestion(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository).findById(999L);
        verify(questionRepository, never()).save(any());
    }
    @Test
    void shouldReturnQuestionById() {
        // Arrange
        StackUser user = StackUser.builder().id(1L).email("test@email.com").build();
        Question question = Question.builder()
                .id(5L)
                .title("Spring?")
                .content("Explain spring")
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(questionRepository.findById(5L)).thenReturn(Optional.of(question));

        // Act
        QuestionDto result = questionService.getQuestionById(5L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getTitle()).isEqualTo("Spring?");
    }
    @Test
    void shouldThrowWhenQuestionNotFoundById() {
        when(questionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getQuestionById(100L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Question not found");
    }
    @Test
    void shouldReturnAllQuestions() {
        StackUser user = StackUser.builder().id(1L).email("a@test.com").build();
        List<Question> questions = List.of(
                Question.builder().id(1L).title("Q1").content("C1").author(user).createdAt(LocalDateTime.now()).build(),
                Question.builder().id(2L).title("Q2").content("C2").author(user).createdAt(LocalDateTime.now()).build()
        );

        when(questionRepository.findAll()).thenReturn(questions);

        List<QuestionDto> result = questionService.getAllQuestions();

        assertThat(result).hasSize(2);
    }
    @Test
    void shouldUpdateQuestionSuccessfully() {
        // Arrange
        StackUser user = StackUser.builder().id(1L).email("user@test.com").build();

        Question existing = Question.builder()
                .id(1L)
                .title("Old Title")
                .content("Old Content")
                .author(user)
                .build();

        Question updated = Question.builder()
                .id(1L)
                .title("New Title")
                .content("New Content")
                .author(user)
                .build();

        QuestionDto dto = new QuestionDto();
        dto.setTitle("New Title");
        dto.setContent("New Content");

        User userDetails=new User("user@test.com","123456",List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities()));

        when(questionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(questionRepository.save(existing)).thenReturn(updated);

        // Act
        QuestionDto result = questionService.updateQuestion(1L, dto);

        // Assert
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getContent()).isEqualTo("New Content");
    }
    @Test
    void shouldThrowWhenUpdatingNonExistingQuestion() {
        // Arrange
        QuestionDto dto = new QuestionDto();
        dto.setTitle("Any");
        dto.setContent("Any");
        User userDetails=new User("user@test.com","123456",List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities()));

        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> questionService.updateQuestion(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Question not found");
    }

    @Test
    void shouldDeleteQuestionSuccessfully() {
        // Arrange
        StackUser user = StackUser.builder().id(1L).email("test@a.com").build();

        Question question = Question.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .author(user)
                .build();

        User userDetails=new User("user@test.com","123456",List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities()));

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        doNothing().when(questionRepository).delete(question);

        // Act
        questionService.deleteQuestion(1L);

        // Assert
        verify(questionRepository, times(1)).delete(question);
    }
    @Test
    void shouldThrowWhenDeletingNonExistingQuestion() {
        when(questionRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.deleteQuestion(100L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Question not found");

        verify(questionRepository, never()).delete(any());
    }

}
