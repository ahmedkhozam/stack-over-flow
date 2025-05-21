package com.example.demo.service;


import com.example.demo.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addCommentToQuestion(Long questionId, CommentDto dto);
    CommentDto addCommentToAnswer(Long answerId, CommentDto dto);
    List<CommentDto> getCommentsForQuestion(Long questionId);
    List<CommentDto> getCommentsForAnswer(Long answerId);
}
