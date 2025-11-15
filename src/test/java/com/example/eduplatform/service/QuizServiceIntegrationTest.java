package com.example.eduplatform.service;

import com.example.eduplatform.entity.*;
import com.example.eduplatform.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class QuizServiceIntegrationTest {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void quizSubmissionCalculatesScore() {
        User student = userRepository.save(User.builder()
                .name("QuizStudent")
                .email("quizstudent@test.example")
                .role(UserRole.STUDENT)
                .build());

        Quiz quiz = quizRepository.save(Quiz.builder()
                .title("Simple quiz")
                .timeLimitMinutes(5)
                .build());

        Question question = questionRepository.save(Question.builder()
                .quiz(quiz)
                .text("2+2=?")
                .type(QuestionType.SINGLE_CHOICE)
                .build());

        AnswerOption correct = answerOptionRepository.save(AnswerOption.builder()
                .question(question)
                .text("4")
                .correct(true)
                .build());

        answerOptionRepository.save(AnswerOption.builder()
                .question(question)
                .text("5")
                .correct(false)
                .build());

        Map<Long, java.util.List<Long>> answers =
                Map.of(question.getId(), List.of(correct.getId()));

        QuizSubmission submission = quizService.submitQuiz(
                new QuizService.SubmitQuizRequest(quiz.getId(), student.getId(), answers));

        assertThat(submission.getId()).isNotNull();
        assertThat(submission.getScore()).isEqualTo(1);
    }
}
