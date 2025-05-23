package com.example.demo.service.impl;


import com.example.demo.dto.AnswerDto;
import com.example.demo.dto.QuestionDto;
import com.example.demo.dto.StackUserDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.StackUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StackUserServiceImpl implements StackUserService {

    private final StackUserRepository stackUserRepository;
    private final StackUserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    @Override
    public StackUserDto createStackUser(StackUserDto stackUserDto) {
        if (stackUserRepository.existsByEmail(stackUserDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        StackUser stackUser = StackUser.builder()
                .username(stackUserDto.getUsername())
                .email(stackUserDto.getEmail())
                .password(stackUserDto.getPassword()) // هنشفره لاحقًا مع Spring Security
                .bio(stackUserDto.getBio())
                .build();

        StackUser savedStackUser = stackUserRepository.save(stackUser);


        return mapToDto(savedStackUser);
    }

    @Override
    public StackUserDto getStackUserById(Long userId) {
        StackUser stackUser = stackUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StackUser not found with id: " + userId));

        return mapToDto(stackUser);
    }

    @Override
    public StackUserDto updateStackUser(Long userId, StackUserDto stackUserDto) {
        StackUser stackUser = stackUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StackUser not found with id: " + userId));

        // نحدث الفيلدز اللي مسموح بتحديثها
        stackUser.setUsername(stackUserDto.getUsername());
        stackUser.setEmail(stackUserDto.getEmail());
        stackUser.setBio(stackUserDto.getBio());
        // الباسورد مش هنحدثه هنا (نعمله EndPoint خاص بيه)

        StackUser updatedStackUser = stackUserRepository.save(stackUser);

        return mapToDto(updatedStackUser);
    }

    @Override
    public void deleteStackUser(Long userId) {
        StackUser stackUser = stackUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StackUser not found with id: " + userId));

        stackUserRepository.delete(stackUser);
    }

    @Override
    public List<StackUserDto> getAllStackUsers() {
        return stackUserRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }
    @Override
    public UserProfileDto getCurrentUserProfile() {
        String email = SecurityUtil.getCurrentUserEmail();
        StackUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Question> questions = questionRepository.findByAuthorId(user.getId());
        List<Answer> answers = answerRepository.findByAuthorId(user.getId());
        List<Comment> comments = commentRepository.findByAuthorId(user.getId());

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setReputation(user.getReputation());

        dto.setQuestionCount(questions.size());
        dto.setAnswerCount(answers.size());
        dto.setCommentCount(comments.size());

        dto.setQuestions(questions.stream().map(this::mapQuestion).toList());
        dto.setAnswers(answers.stream().map(this::mapAnswer).toList());

        return dto;
    }

    private QuestionDto mapQuestion(Question q) {
        QuestionDto dto = new QuestionDto();
        BeanUtils.copyProperties(q, dto);
        dto.setAuthorId(q.getAuthor().getId());
        return dto;
    }

    private AnswerDto mapAnswer(Answer a) {
        AnswerDto dto = new AnswerDto();
        BeanUtils.copyProperties(a, dto);
        dto.setAuthorId(a.getAuthor().getId());
        dto.setQuestionId(a.getQuestion().getId());
        return dto;
    }


    private StackUserDto mapToDto(StackUser stackUser) {
        StackUserDto stackUserDto = new StackUserDto();
        BeanUtils.copyProperties(stackUser, stackUserDto);
        return stackUserDto;
    }
}
