package com.learnpulse.domain.repository

import com.learnpulse.domain.model.Quiz
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun getQuizForLesson(lessonId: String): Quiz?

    suspend fun generateAiQuiz(topic: String, difficulty: String, questionCount: Int): Result<Quiz>

    fun getSavedQuizzes(userId: String): Flow<List<Quiz>>

    suspend fun saveQuizResult(userId: String, quizId: String, score: Int): Result<Unit>
}
