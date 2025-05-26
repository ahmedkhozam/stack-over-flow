package com.example.demo.controller;


import com.example.demo.dto.CommentDto;
import com.example.demo.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //  تعليق على سؤال
    @PostMapping("/question/{id}")
    public ResponseEntity<CommentDto> commentOnQuestion(@PathVariable Long id,
                                                        @Valid @RequestBody CommentDto dto) {
        return ResponseEntity.ok(commentService.addCommentToQuestion(id, dto));
    }

    //  تعليق على إجابة
    @PostMapping("/answer/{id}")
    public ResponseEntity<CommentDto> commentOnAnswer(@PathVariable Long id,
                                                      @Valid @RequestBody CommentDto dto) {
        return ResponseEntity.ok(commentService.addCommentToAnswer(id, dto));
    }

    //  عرض كل تعليقات سؤال
    @GetMapping("/question/{id}")
    public ResponseEntity<List<CommentDto>> getCommentsForQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsForQuestion(id));
    }

    //  عرض كل تعليقات إجابة
    @GetMapping("/answer/{id}")
    public ResponseEntity<List<CommentDto>> getCommentsForAnswer(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsForAnswer(id));
    }
}
