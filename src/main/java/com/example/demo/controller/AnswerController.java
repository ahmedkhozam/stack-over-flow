package com.example.demo.controller;

import com.example.demo.dto.AnswerDto;
import com.example.demo.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // إضافة إجابة لسؤال
    @PostMapping("/question/{questionId}")
    public ResponseEntity<AnswerDto> addAnswer(@PathVariable Long questionId,
                                               @Valid @RequestBody AnswerDto dto) {
        AnswerDto created = answerService.addAnswer(questionId, dto);
        return ResponseEntity.ok(created);
    }

    // عرض كل الإجابات لسؤال معين
    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<AnswerDto>> getAnswersByQuestion(@PathVariable Long questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestionId(questionId));
    }
    // تعديل إجابة
    @PutMapping("/{id}")
    public ResponseEntity<AnswerDto> updateAnswer(@PathVariable Long id,
                                                  @Valid @RequestBody AnswerDto dto) {
        return ResponseEntity.ok(answerService.updateAnswer(id, dto));
    }

    // حذف إجابة
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long id) {
        answerService.deleteAnswer(id);
        return ResponseEntity.noContent().build();
    }

}
