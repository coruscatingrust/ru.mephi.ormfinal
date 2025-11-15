package com.example.eduplatform.service;

import com.example.eduplatform.entity.*;
import com.example.eduplatform.repository.AssignmentRepository;
import com.example.eduplatform.repository.LessonRepository;
import com.example.eduplatform.repository.SubmissionRepository;
import com.example.eduplatform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public Assignment createAssignment(Long lessonId, CreateAssignmentRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found: " + lessonId));

        Assignment assignment = Assignment.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .maxScore(request.maxScore())
                .lesson(lesson)
                .build();
        return assignmentRepository.save(assignment);
    }

    public Submission submitAssignment(Long assignmentId, Long studentId, SubmitAssignmentRequest request) {
        if (submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId).isPresent()) {
            throw new IllegalStateException("Assignment already submitted by this student");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found: " + assignmentId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .submittedAt(LocalDateTime.now())
                .content(request.content())
                .build();
        return submissionRepository.save(submission);
    }

    public Submission gradeSubmission(Long submissionId, GradeSubmissionRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found: " + submissionId));
        submission.setScore(request.score());
        submission.setFeedback(request.feedback());
        return submissionRepository.save(submission);
    }

    public record CreateAssignmentRequest(
            @NotBlank String title,
            String description,
            @NotNull LocalDateTime dueDate,
            @NotNull Integer maxScore
    ) {
    }

    public record SubmitAssignmentRequest(
            @NotBlank String content
    ) {
    }

    public record GradeSubmissionRequest(
            @NotNull Integer score,
            String feedback
    ) {
    }
}
