package com.example.eduplatform.service;

import com.example.eduplatform.entity.*;
import com.example.eduplatform.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public Course createCourse(CreateCourseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.categoryId()));
        User teacher = userRepository.findById(request.teacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found: " + request.teacherId()));

        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .category(category)
                .teacher(teacher)
                .build();
        return courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + id));
    }

    public CourseModule addModule(CreateModuleRequest request) {
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + request.courseId()));

        CourseModule module = CourseModule.builder()
                .title(request.title())
                .orderIndex(request.orderIndex())
                .course(course)
                .build();
        moduleRepository.save(module);
        course.getModules().add(module);
        return module;
    }

    public Lesson addLesson(CreateLessonRequest request) {
        CourseModule module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found: " + request.moduleId()));

        Lesson lesson = Lesson.builder()
                .title(request.title())
                .content(request.content())
                .videoUrl(request.videoUrl())
                .module(module)
                .build();
        lessonRepository.save(lesson);
        module.getLessons().add(lesson);
        return lesson;
    }

    public long countCourses() {
        return courseRepository.count();
    }

    public record CreateCourseRequest(
            @NotBlank String title,
            String description,
            @NotNull Long categoryId,
            @NotNull Long teacherId
    ) {
    }

    public record CreateModuleRequest(
            Long courseId,            // id придёт из @PathVariable в контроллере
            @NotBlank String title,
            Integer orderIndex
    ) {
    }


    public record CreateLessonRequest(
            Long moduleId,            // id также берём из @PathVariable
            @NotBlank String title,
            String content,
            String videoUrl
    ) {
    }
}
