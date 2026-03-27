package com.learnpulse.domain.repository

import com.learnpulse.domain.model.LearningStats
import com.learnpulse.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getProgress(userId: String, courseId: String): Flow<UserProgress?>

    fun getAllProgress(userId: String): Flow<List<UserProgress>>

    suspend fun markLessonComplete(userId: String, courseId: String, lessonId: String): Result<Unit>

    suspend fun saveQuizScore(userId: String, courseId: String, lessonId: String, score: Int): Result<Unit>

    suspend fun updateLastAccessed(userId: String, courseId: String, lessonId: String): Result<Unit>

    fun getLearningStats(userId: String): Flow<LearningStats>

    suspend fun recordLearningTime(userId: String, courseId: String, seconds: Long): Result<Unit>
}
