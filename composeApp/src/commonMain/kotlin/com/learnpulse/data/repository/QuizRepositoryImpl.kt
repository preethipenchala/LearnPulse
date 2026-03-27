package com.learnpulse.data.repository

import com.learnpulse.data.remote.api.LearnPulseApi
import com.learnpulse.data.remote.dto.GenerateQuizRequest
import com.learnpulse.data.remote.dto.toDomain
import com.learnpulse.domain.model.Quiz
import com.learnpulse.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class QuizRepositoryImpl(
    private val api: LearnPulseApi
) : QuizRepository {

    private val savedQuizzes = mutableListOf<Quiz>()

    override suspend fun getQuizForLesson(lessonId: String): Quiz? {
        return try {
            api.getQuizForLesson(lessonId).toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun generateAiQuiz(topic: String, difficulty: String, questionCount: Int): Result<Quiz> {
        return try {
            val quiz = api.generateAiQuiz(
                GenerateQuizRequest(topic, difficulty, questionCount)
            ).toDomain()
            savedQuizzes.add(quiz)
            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getSavedQuizzes(userId: String): Flow<List<Quiz>> =
        flowOf(savedQuizzes.toList())

    override suspend fun saveQuizResult(userId: String, quizId: String, score: Int): Result<Unit> =
        Result.success(Unit)
}
