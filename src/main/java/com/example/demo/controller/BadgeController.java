package com.example.demo.controller;

import com.example.demo.dto.BadgeResponse;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @GetMapping("/me")
    public List<BadgeResponse> getMyBadges() {
        String email = SecurityUtil.getCurrentUserEmail();
        return badgeService.getBadgesForUser(email);
    }
}
