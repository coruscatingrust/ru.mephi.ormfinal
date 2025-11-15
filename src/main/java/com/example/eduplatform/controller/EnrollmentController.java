package com.example.eduplatform.controller;

import com.example.eduplatform.entity.Enrollment;
import com.example.eduplatform.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<Enrollment> enroll(@RequestBody @Valid EnrollmentService.EnrollRequest request) {
        Enrollment enrollment = enrollmentService.enrollStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unenroll(@PathVariable Long id) {
        enrollmentService.unenroll(id);
    }

    @GetMapping("/by-course/{courseId}")
    public List<Enrollment> byCourse(@PathVariable Long courseId) {
        return enrollmentService.findByCourse(courseId);
    }

    @GetMapping("/by-student/{studentId}")
    public List<Enrollment> byStudent(@PathVariable Long studentId) {
        return enrollmentService.findByStudent(studentId);
    }
}
