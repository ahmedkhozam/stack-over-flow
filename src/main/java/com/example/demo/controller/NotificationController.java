package com.example.demo.controller;

import com.example.demo.dto.NotificationDto;
import com.example.demo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //  عرض إشعارات المستخدم
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    //  تعليم إشعار كمقروء
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }
}
