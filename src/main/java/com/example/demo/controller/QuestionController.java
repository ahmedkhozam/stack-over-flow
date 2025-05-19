package com.example.demo.controller;

import com.example.demo.dto.QuestionDto;
import com.example.demo.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // إضافة سؤال جديد
    @PostMapping
    public ResponseEntity<QuestionDto> createQuestion(@Valid @RequestBody QuestionDto dto) {
        QuestionDto created = questionService.createQuestion(dto);
        return ResponseEntity.ok(created);
    }

    // عرض سؤال حسب الـ ID
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    // عرض كل الأسئلة
    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    // تعديل سؤال
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Long id,
                                                      @Valid @RequestBody QuestionDto dto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, dto));
    }

    // حذف سؤال
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

}
