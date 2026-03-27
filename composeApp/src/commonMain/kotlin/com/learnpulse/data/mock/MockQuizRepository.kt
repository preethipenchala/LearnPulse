package com.learnpulse.data.mock

import com.learnpulse.domain.model.Quiz
import com.learnpulse.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MockQuizRepository : QuizRepository {

    private val savedQuizzes = MutableStateFlow<List<Quiz>>(emptyList())

    override suspend fun getQuizForLesson(lessonId: String): Quiz? =
        MockDataSource.quizzes[lessonId]

    override suspend fun generateAiQuiz(topic: String, difficulty: String, questionCount: Int): Result<Quiz> {
        val quiz = MockDataSource.generateAiQuiz(topic, difficulty, questionCount)
        savedQuizzes.value = savedQuizzes.value + quiz
        return Result.success(quiz)
    }

    override fun getSavedQuizzes(userId: String): Flow<List<Quiz>> = savedQuizzes.map { it.toList() }

    override suspend fun saveQuizResult(userId: String, quizId: String, score: Int): Result<Unit> =
        Result.success(Unit)
}
