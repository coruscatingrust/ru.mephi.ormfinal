package com.example.eduplatform.controller;

import com.example.eduplatform.entity.Course;
import com.example.eduplatform.entity.CourseModule;
import com.example.eduplatform.entity.Lesson;
import com.example.eduplatform.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody @Valid CourseService.CreateCourseRequest request) {
        Course created = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @PostMapping("/{id}/modules")
    public ResponseEntity<CourseModule> addModule(@PathVariable("id") Long courseId,
                                                  @RequestBody @Valid CourseService.CreateModuleRequest request) {
        CourseService.CreateModuleRequest fixed =
                new CourseService.CreateModuleRequest(courseId, request.title(), request.orderIndex());
        CourseModule module = courseService.addModule(fixed);
        return ResponseEntity.status(HttpStatus.CREATED).body(module);
    }

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Lesson> addLesson(@PathVariable Long moduleId,
                                            @RequestBody @Valid CourseService.CreateLessonRequest request) {
        CourseService.CreateLessonRequest fixed =
                new CourseService.CreateLessonRequest(moduleId, request.title(), request.content(), request.videoUrl());
        Lesson lesson = courseService.addLesson(fixed);
        return ResponseEntity.status(HttpStatus.CREATED).body(lesson);
    }
}
