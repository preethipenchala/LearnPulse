package com.learnpulse.domain.usecase

import com.learnpulse.domain.model.Quiz
import com.learnpulse.domain.repository.QuizRepository

class GenerateQuizUseCase(private val quizRepository: QuizRepository) {
    suspend operator fun invoke(topic: String, difficulty: String, questionCount: Int = 10): Result<Quiz> =
        quizRepository.generateAiQuiz(topic, difficulty, questionCount)
}
