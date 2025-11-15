package com.example.eduplatform.service;

import com.example.eduplatform.entity.*;
import com.example.eduplatform.repository.CategoryRepository;
import com.example.eduplatform.repository.CourseRepository;
import com.example.eduplatform.repository.EnrollmentRepository;
import com.example.eduplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnrollmentServiceIntegrationTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    void studentCanEnrollOnlyOnce() {
        Category category = categoryRepository.save(Category.builder()
                .name("Databases")
                .build());

        User teacher = userRepository.save(User.builder()
                .name("Teacher")
                .email("teacher2@test.example")
                .role(UserRole.TEACHER)
                .build());

        User student = userRepository.save(User.builder()
                .name("Student")
                .email("student2@test.example")
                .role(UserRole.STUDENT)
                .build());

        Course course = courseRepository.save(Course.builder()
                .title("ORM")
                .description("ORM course")
                .category(category)
                .teacher(teacher)
                .build());

        Enrollment enrollment = enrollmentService.enrollStudent(
                new EnrollmentService.EnrollRequest(course.getId(), student.getId()));

        assertThat(enrollment.getId()).isNotNull();
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);

        assertThatThrownBy(() ->
                enrollmentService.enrollStudent(
                        new EnrollmentService.EnrollRequest(course.getId(), student.getId())))
                .isInstanceOf(IllegalStateException.class);

        assertThat(enrollmentRepository.findByStudentId(student.getId())).hasSize(1);
    }
}
