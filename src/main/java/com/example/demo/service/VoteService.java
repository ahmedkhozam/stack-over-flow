package com.example.demo.service;

public interface VoteService {
    void voteOnQuestion(Long questionId, int value);
    void voteOnAnswer(Long answerId, int value);

    void voteOnComment(Long commentId, int value);
}
