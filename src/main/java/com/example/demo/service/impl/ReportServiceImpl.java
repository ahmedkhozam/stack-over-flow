package com.example.demo.service.impl;

import com.example.demo.dto.ReportRequest;
import com.example.demo.dto.ReportResponse;
import com.example.demo.entity.*;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.*;
import com.example.demo.security.SecurityUtil;
import com.example.demo.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final StackUserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    @Override
    public ReportResponse reportContent(ReportRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        StackUser reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Report report = new Report();
        report.setReason(request.getReason());
        report.setReporter(reporter);
        report.setReportedAt(LocalDateTime.now());

        if (request.getQuestionId() != null) {
            Question question = questionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
            report.setQuestion(question);
        } else if (request.getAnswerId() != null) {
            Answer answer = answerRepository.findById(request.getAnswerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));
            report.setAnswer(answer);
        } else if (request.getCommentId() != null) {
            Comment comment = commentRepository.findById(request.getCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
            report.setComment(comment);
        } else {
            throw new IllegalArgumentException("You must report a question, answer, or comment");
        }

        Report savedReport = reportRepository.save(report);

        return ReportResponse.builder()
                .id(savedReport.getId())
                .reason(savedReport.getReason())
                .reporterEmail(reporter.getEmail())
                .reportedAt(savedReport.getReportedAt())
                .questionId(savedReport.getQuestion() != null ? savedReport.getQuestion().getId() : null)
                .answerId(savedReport.getAnswer() != null ? savedReport.getAnswer().getId() : null)
                .commentId(savedReport.getComment() != null ? savedReport.getComment().getId() : null)
                .build();
    }
}
