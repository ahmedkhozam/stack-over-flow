package com.example.demo.service;


import com.example.demo.dto.BadgeResponse;
import com.example.demo.entity.Answer;
import com.example.demo.entity.StackUser;

import java.util.List;

public interface BadgeService {
    void checkAndAssignBadgesForAnswer(StackUser user, Answer answer);
    List<BadgeResponse> getBadgesForUser(String email);

}
