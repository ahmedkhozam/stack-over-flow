package com.example.demo.service.impl;

import com.example.demo.dto.NotificationDto;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Question;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final StackUserRepository userRepository;


    @Override
    public void notifyUser(Long recipientId, String message, Long questionId, Long answerId) {
        StackUser recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .message(message)
                .recipient(recipient)
                .question(questionId != null ? Question.builder().id(questionId).build() : null)
                .answer(answerId != null ? Answer.builder().id(answerId).build() : null)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDto> getMyNotifications() {
        String email = SecurityUtil.getCurrentUserEmail();
        StackUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationDto mapToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        if (notification.getQuestion() != null) {
            dto.setQuestionId(notification.getQuestion().getId());
        }
        if (notification.getAnswer() != null) {
            dto.setAnswerId(notification.getAnswer().getId());
        }
        return dto;
    }
}
