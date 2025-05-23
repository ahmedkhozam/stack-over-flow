package com.example.demo.service;


import com.example.demo.dto.NotificationDto;

import java.util.List;

public interface NotificationService {

    void notifyUser(Long recipientId, String message, Long questionId, Long answerId);

    List<NotificationDto> getMyNotifications();

    void markAsRead(Long notificationId);
}
