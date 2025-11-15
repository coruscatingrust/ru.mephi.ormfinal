package com.example.eduplatform.service;

import com.example.eduplatform.entity.*;
import com.example.eduplatform.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AssignmentServiceIntegrationTest {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Test
    void studentSubmitsAssignment() {
        Category category = categoryRepository.save(Category.builder()
                .name("Programming 2")
                .build());

        User teacher = userRepository.save(User.builder()
                .name("Teacher3")
                .email("teacher3@test.example")
                .role(UserRole.TEACHER)
                .build());

        User student = userRepository.save(User.builder()
                .name("Student3")
                .email("student3@test.example")
                .role(UserRole.STUDENT)
                .build());

        Course course = courseRepository.save(Course.builder()
                .title("Spring ORM")
                .description("Spring with ORM")
                .category(category)
                .teacher(teacher)
                .build());

        CourseModule module = moduleRepository.save(CourseModule.builder()
                .title("Module 1")
                .orderIndex(1)
                .course(course)
                .build());

        Lesson lesson = lessonRepository.save(Lesson.builder()
                .title("Lesson 1")
                .content("Content")
                .module(module)
                .build());

        Assignment assignment = assignmentService.createAssignment(
                lesson.getId(),
                new AssignmentService.CreateAssignmentRequest(
                        "HW1",
                        "Do something",
                        LocalDateTime.now().plusDays(3),
                        100
                ));

        Submission submission = assignmentService.submitAssignment(
                assignment.getId(),
                student.getId(),
                new AssignmentService.SubmitAssignmentRequest("My solution"));

        assertThat(submission.getId()).isNotNull();
        assertThat(submissionRepository.findByAssignmentIdAndStudentId(
                assignment.getId(), student.getId())).isPresent();
    }
}
