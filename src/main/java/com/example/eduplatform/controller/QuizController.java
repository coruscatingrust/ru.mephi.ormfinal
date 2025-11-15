package com.example.eduplatform.controller;

import com.example.eduplatform.entity.QuizSubmission;
import com.example.eduplatform.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizSubmission> submitQuiz(@PathVariable Long quizId,
                                                     @RequestBody @Valid QuizService.SubmitQuizRequest request) {
        QuizService.SubmitQuizRequest fixed =
                new QuizService.SubmitQuizRequest(quizId, request.studentId(), request.answers());
        QuizSubmission submission = quizService.submitQuiz(fixed);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @GetMapping("/submissions/by-student/{studentId}")
    public List<QuizSubmission> byStudent(@PathVariable Long studentId) {
        return quizService.findByStudent(studentId);
    }

    @GetMapping("/submissions/by-quiz/{quizId}")
    public List<QuizSubmission> byQuiz(@PathVariable Long quizId) {
        return quizService.findByQuiz(quizId);
    }
}
