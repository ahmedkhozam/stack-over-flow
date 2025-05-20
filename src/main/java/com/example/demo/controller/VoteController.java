package com.example.demo.controller;

import com.example.demo.dto.VoteRequest;
import com.example.demo.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/question/{id}")
    public ResponseEntity<String> voteOnQuestion(@PathVariable Long id,
                                                 @Valid @RequestBody VoteRequest request) {
        if (request.getValue() != 1 && request.getValue() != -1) {
            return ResponseEntity.badRequest().body("Vote value must be +1 or -1");
        }

        voteService.voteOnQuestion(id, request.getValue());
        return ResponseEntity.ok("Vote recorded for question");
    }

    @PostMapping("/answer/{id}")
    public ResponseEntity<String> voteOnAnswer(@PathVariable Long id,
                                               @Valid @RequestBody VoteRequest request) {
        if (request.getValue() != 1 && request.getValue() != -1) {
            return ResponseEntity.badRequest().body("Vote value must be +1 or -1");
        }

        voteService.voteOnAnswer(id, request.getValue());
        return ResponseEntity.ok("Vote recorded for answer");
    }
}
