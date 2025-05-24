package com.example.demo.service.impl;


import com.example.demo.dto.AnswerDto;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Bounty;
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

    @Mock
    private BountyRepository bountyRepository;

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

    @Test
    void shouldIncreaseReputationByBountyWhenAnswerAccepted() {
        // Arrange

        // المستخدم صاحب السؤال (current user) هو الذي سيقبل الإجابة.
        StackUser questionOwner = StackUser.builder()
                .id(1L)
                .email("owner@test.com")
                .username("QuestionOwner")
                .reputation(100)  // مثلاً سمعة مبدئية 100
                .build();

        // السؤال الذي تم طرحه بواسطة صاحب السؤال
        Question question = Question.builder()
                .id(10L)
                .title("How to implement bounty?")
                .author(questionOwner)
                .build();

        // إنشاء باونتي للسؤال (مثلاً 50 نقطة) وارتباطه بالسؤال والمستخدم
        Bounty bounty = Bounty.builder()
                .id(5L)
                .amount(50)
                .expiry(LocalDateTime.now().plusDays(1))
                .user(questionOwner)
                .question(question)
                .build();
        question.setBounty(bounty);

        // إنشاء إجابة من مستخدم آخر
        StackUser answerOwner = StackUser.builder()
                .id(2L)
                .email("answer@test.com")
                .username("AnswerOwner")
                .reputation(80)  // سمعة مبدئية 80
                .build();

        Answer answer = Answer.builder()
                .id(20L)
                .content("This is an answer")
                .accepted(false)
                .author(answerOwner)
                .question(question)
                .build();

        // Mock الإيجاد:
        when(answerRepository.findById(20L)).thenReturn(Optional.of(answer));
        // لأن getCurrentUser() تستخدم findByEmail() للحصول على صاحب السؤال
        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.of(questionOwner));
        when(questionRepository.findById(10L)).thenReturn(Optional.of(question));
        // عادة bountyRepository لا تحتاج mock إضافي إننا نستخدم delete()
        // لكن يمكننا عمل mock للـ bountyRepository.delete(bounty) على أنه void:
        doNothing().when(bountyRepository).delete(bounty);

        // استخدم mock static لـ SecurityUtil:
        try (MockedStatic<SecurityUtil> mockedStatic = Mockito.mockStatic(SecurityUtil.class)) {
            // بما أن قبول الإجابة يتم بواسطة صاحب السؤال، فـ السطر التالي يجب أن يرجع إيميل صاحب السؤال.
            mockedStatic.when(SecurityUtil::getCurrentUserEmail).thenReturn("owner@test.com");

            // Act: قبول الإجابة
            answerService.acceptAnswer(20L);

            // Assert:
            // تأكد إن الإجابة أصبحت Accepted
            assertThat(answer.isAccepted()).isTrue();

            // تأكد إن سمعة صاحب الإجابة زادت بمقدار 50 نقطة:
            // سمعة الإجابة المحدثة يجب أن تكون 80 + 50 = 130
            verify(userRepository).save(argThat(userSaved ->
                    userSaved.getId().equals(2L) && userSaved.getReputation() == 130
            ));

            // تأكد من حذف الباونتي:
            verify(bountyRepository).delete(bounty);

            // تأكد إن السؤال بقا بدون باونتي:
            assertThat(question.getBounty()).isNull();
        }
    }
}
