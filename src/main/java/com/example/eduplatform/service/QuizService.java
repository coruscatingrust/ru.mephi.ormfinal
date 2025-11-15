package com.example.eduplatform.service;

import com.example.eduplatform.entity.AnswerOption;
import com.example.eduplatform.entity.Quiz;
import com.example.eduplatform.entity.QuizSubmission;
import com.example.eduplatform.entity.User;
import com.example.eduplatform.repository.AnswerOptionRepository;
import com.example.eduplatform.repository.QuizRepository;
import com.example.eduplatform.repository.QuizSubmissionRepository;
import com.example.eduplatform.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;
    private final AnswerOptionRepository answerOptionRepository;

    /**
     * Подсчёт балла:
     *  - идём по всем ответам из request.answers;
     *  - для каждого выбранного варианта поднимаем AnswerOption из БД;
     *  - если все выбранные варианты помечены как correct=true, вопрос засчитывается как 1 балл.
     *  - (для простоты не проверяем, выбраны ли "все" правильные варианты, нам важна только корректность выбранных).
     */
    public QuizSubmission submitQuiz(SubmitQuizRequest request) {
        Quiz quiz = quizRepository.findById(request.quizId())
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found: " + request.quizId()));
        User student = userRepository.findById(request.studentId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + request.studentId()));

        int score = 0;

        for (Map.Entry<Long, List<Long>> entry : request.answers().entrySet()) {
            List<Long> selectedOptionIds = entry.getValue();
            if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
                continue;
            }

            boolean allSelectedCorrect = true;

            for (Long optionId : selectedOptionIds) {
                AnswerOption option = answerOptionRepository.findById(optionId)
                        .orElseThrow(() -> new EntityNotFoundException("Answer option not found: " + optionId));

                if (!option.isCorrect()) {
                    allSelectedCorrect = false;
                    break;
                }
            }

            if (allSelectedCorrect) {
                score++;
            }
        }

        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .student(student)
                .score(score)
                .takenAt(LocalDateTime.now())
                .build();

        return quizSubmissionRepository.save(submission);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> findByStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<QuizSubmission> findByQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId);
    }

    public record SubmitQuizRequest(
            Long quizId,                             // в REST-контроллере берётся из @PathVariable, в тесте передаётся явно
            @NotNull Long studentId,
            @NotNull Map<Long, List<Long>> answers
    ) {
    }
}
