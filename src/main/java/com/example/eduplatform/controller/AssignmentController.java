package com.example.eduplatform.controller;

import com.example.eduplatform.entity.Assignment;
import com.example.eduplatform.entity.Submission;
import com.example.eduplatform.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/lesson/{lessonId}")
    public ResponseEntity<Assignment> createAssignment(@PathVariable Long lessonId,
                                                       @RequestBody @Valid AssignmentService.CreateAssignmentRequest request) {
        Assignment assignment = assignmentService.createAssignment(lessonId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    @PostMapping("/{assignmentId}/submit/{studentId}")
    public ResponseEntity<Submission> submitAssignment(@PathVariable Long assignmentId,
                                                       @PathVariable Long studentId,
                                                       @RequestBody @Valid AssignmentService.SubmitAssignmentRequest request) {
        Submission submission = assignmentService.submitAssignment(assignmentId, studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public Submission gradeSubmission(@PathVariable Long submissionId,
                                      @RequestBody @Valid AssignmentService.GradeSubmissionRequest request) {
        return assignmentService.gradeSubmission(submissionId, request);
    }
}
