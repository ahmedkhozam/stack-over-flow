package com.example.demo.controller;

import com.example.demo.dto.QuestionDto;
import com.example.demo.dto.QuestionRequest;
import com.example.demo.dto.QuestionResponse;
import com.example.demo.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> addQuestion(@RequestBody @Valid QuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long id) {
        QuestionResponse response = questionService.getQuestionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }
    // تعديل سؤال
    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponse> updateQuestion(@PathVariable Long id,
                                                      @Valid @RequestBody QuestionRequest dto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, dto));
    }

    // حذف سؤال
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

}
