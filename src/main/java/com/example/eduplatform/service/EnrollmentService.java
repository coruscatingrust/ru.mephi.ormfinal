package com.example.eduplatform.service;

import com.example.eduplatform.entity.*;
import com.example.eduplatform.repository.CourseRepository;
import com.example.eduplatform.repository.EnrollmentRepository;
import com.example.eduplatform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public Enrollment enrollStudent(EnrollRequest request) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(request.studentId(), request.courseId())) {
            throw new IllegalStateException("Student already enrolled to course");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + request.courseId()));
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + request.studentId()));

        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .student(student)
                .enrolledAt(LocalDateTime.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();
        return enrollmentRepository.save(enrollment);
    }

    public void unenroll(Long enrollmentId) {
        enrollmentRepository.deleteById(enrollmentId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Transactional(readOnly = true)
    public List<Enrollment> findByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public record EnrollRequest(
            @NotNull Long courseId,
            @NotNull Long studentId
    ) {
    }
}
