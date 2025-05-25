package com.example.demo.repository;

import com.example.demo.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByVoterIdAndQuestionId(Long userId, Long questionId);
    Optional<Vote> findByVoterIdAndAnswerId(Long userId, Long answerId);

    int countByQuestionIdAndValue(Long questionId, int value);
    int countByAnswerIdAndValue(Long answerId, int value);

    Optional<Vote> findByVoterIdAndCommentId(Long voterId, Long commentId);
    int countByCommentIdAndValue(Long commentId, int value);

}
