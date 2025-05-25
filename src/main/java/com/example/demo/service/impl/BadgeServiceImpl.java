package com.example.demo.service.impl;

import com.example.demo.dto.BadgeResponse;
import com.example.demo.entity.Answer;
import com.example.demo.entity.StackUser;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.StackUserRepository;
import com.example.demo.repository.VoteRepository;
import com.example.demo.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final VoteRepository voteRepository;
    private final StackUserRepository userRepository;

    @Override
    public void checkAndAssignBadgesForAnswer(StackUser user, Answer answer) {
        int upvotes = voteRepository.countByAnswerIdAndValue(answer.getId(), 1);

        // شرط منح شارة "Nice Answer"
        if (upvotes >= 10) {
            badgeRepository.findByName("Nice Answer").ifPresent(badge -> {
                if (!user.getBadges().contains(badge)) {
                    user.getBadges().add(badge);
                    userRepository.save(user);
                }
            });
        }

        // ممكن تضيف شارات تانية هنا بنفس الفكرة 👇
        // if (upvotes >= 25) { ... }
    }

    @Override
    public List<BadgeResponse> getBadgesForUser(String email) {
        StackUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return user.getBadges().stream()
                .map(badge -> new BadgeResponse(
                        badge.getName(),
                        badge.getDescription(),
                        badge.getLevel()
                ))
                .toList();
    }

}
