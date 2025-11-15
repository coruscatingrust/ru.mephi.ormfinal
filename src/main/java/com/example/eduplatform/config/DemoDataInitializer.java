package com.example.eduplatform.config;

import com.example.eduplatform.entity.*;
import com.example.eduplatform.repository.*;
import com.example.eduplatform.service.AssignmentService;
import com.example.eduplatform.service.CourseService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DemoDataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final CourseRepository courseRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (courseRepository.count() > 0) {
            return;
        }

        User teacher = userRepository.save(User.builder()
                .name("Teacher One")
                .email("teacher@example.com")
                .role(UserRole.TEACHER)
                .build());

        userRepository.save(User.builder()
                .name("Student One")
                .email("student@example.com")
                .role(UserRole.STUDENT)
                .build());

        Category category = categoryRepository.save(Category.builder()
                .name("Programming")
                .build());

        Course course = courseService.createCourse(
                new CourseService.CreateCourseRequest(
                        "ORM & Hibernate Basics",
                        "Demo course created on startup",
                        category.getId(),
                        teacher.getId()
                )
        );

        CourseModule module = courseService.addModule(
                new CourseService.CreateModuleRequest(
                        course.getId(),
                        "Introduction to ORM",
                        1
                )
        );

        Lesson lesson = courseService.addLesson(
                new CourseService.CreateLessonRequest(
                        module.getId(),
                        "What is ORM",
                        "Lesson content",
                        null
                )
        );

        assignmentService.createAssignment(
                lesson.getId(),
                new AssignmentService.CreateAssignmentRequest(
                        "First homework",
                        "Implement basic entity mapping",
                        LocalDateTime.now().plusDays(7),
                        100
                )
        );

        Quiz quiz = quizRepository.save(Quiz.builder()
                .module(module)
                .title("Intro quiz")
                .timeLimitMinutes(10)
                .build());

        Question question = questionRepository.save(Question.builder()
                .quiz(quiz)
                .text("ORM stands for?")
                .type(QuestionType.SINGLE_CHOICE)
                .build());

        answerOptionRepository.save(AnswerOption.builder()
                .question(question)
                .text("Object-Relational Mapping")
                .correct(true)
                .build());

        answerOptionRepository.save(AnswerOption.builder()
                .question(question)
                .text("Old Relational Model")
                .correct(false)
                .build());
    }
}
