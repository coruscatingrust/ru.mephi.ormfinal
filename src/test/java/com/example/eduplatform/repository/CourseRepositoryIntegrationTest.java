package com.example.eduplatform.repository;

import com.example.eduplatform.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseRepositoryIntegrationTest {

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

    @Test
    void createCourseWithModuleAndLesson() {
        Category category = categoryRepository.save(Category.builder()
                .name("Programming")
                .build());

        User teacher = userRepository.save(User.builder()
                .name("Teacher")
                .email("teacher@test.example")
                .role(UserRole.TEACHER)
                .build());

        Course course = courseRepository.save(Course.builder()
                .title("Hibernate course")
                .description("Basics")
                .category(category)
                .teacher(teacher)
                .build());

        CourseModule module = moduleRepository.save(CourseModule.builder()
                .title("Intro")
                .orderIndex(1)
                .course(course)
                .build());

        Lesson lesson = lessonRepository.save(Lesson.builder()
                .title("Lesson 1")
                .content("Content")
                .module(module)
                .build());

        Course loaded = courseRepository.findById(course.getId()).orElseThrow();
        assertThat(loaded.getId()).isNotNull();
        assertThat(loaded.getCategory().getName()).isEqualTo("Programming");
        assertThat(loaded.getTeacher().getEmail()).isEqualTo("teacher@test.example");
        assertThat(module.getId()).isNotNull();
        assertThat(lesson.getId()).isNotNull();
    }
}
