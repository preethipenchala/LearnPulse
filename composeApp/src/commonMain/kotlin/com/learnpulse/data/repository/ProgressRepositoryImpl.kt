package com.learnpulse.data.repository

import com.learnpulse.data.local.LocalDataSource
import com.learnpulse.data.remote.api.LearnPulseApi
import com.learnpulse.data.remote.dto.toDomain
import com.learnpulse.domain.model.*
import com.learnpulse.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProgressRepositoryImpl(
    private val api: LearnPulseApi,
    private val localDataSource: LocalDataSource
) : ProgressRepository {

    override fun getProgress(userId: String, courseId: String): Flow<UserProgress?> =
        localDataSource.getProgress(userId, courseId)

    override fun getAllProgress(userId: String): Flow<List<UserProgress>> =
        localDataSource.getAllProgress(userId)

    override suspend fun markLessonComplete(userId: String, courseId: String, lessonId: String): Result<Unit> {
        return try {
            api.markLessonComplete(userId, courseId, lessonId)
            // Update local cache
            val progress = localDataSource.getProgress(userId, courseId).first()
            if (progress != null) {
                val updated = progress.copy(
                    completedLessons = (progress.completedLessons + lessonId).distinct()
                )
                localDataSource.saveProgress(updated)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveQuizScore(userId: String, courseId: String, lessonId: String, score: Int): Result<Unit> {
        return try {
            api.saveQuizScore(userId, courseId, lessonId, score)
            val progress = localDataSource.getProgress(userId, courseId).first()
            if (progress != null) {
                val updated = progress.copy(
                    quizScores = progress.quizScores + (lessonId to score)
                )
                localDataSource.saveProgress(updated)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLastAccessed(userId: String, courseId: String, lessonId: String): Result<Unit> {
        return try {
            val progress = localDataSource.getProgress(userId, courseId).first()
            if (progress != null) {
                val updated = progress.copy(lastAccessedLessonId = lessonId)
                localDataSource.saveProgress(updated)
            } else {
                localDataSource.saveProgress(
                    UserProgress(
                        userId = userId,
                        courseId = courseId,
                        completedLessons = emptyList(),
                        quizScores = emptyMap(),
                        lastAccessedLessonId = lessonId,
                        overallProgress = 0f,
                        certificateEarned = false,
                        streakDays = 0
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLearningStats(userId: String): Flow<LearningStats> =
        localDataSource.getAllProgress(userId).map { progressList ->
            val completed = progressList.count { it.certificateEarned }
            val totalSeconds = progressList.sumOf { it.totalTimeSpentSeconds }
            val streak = progressList.maxOfOrNull { it.streakDays } ?: 0
            LearningStats(
                coursesEnrolled = progressList.size,
                coursesCompleted = completed,
                streakDays = streak,
                certificatesEarned = completed,
                totalLearningMinutes = (totalSeconds / 60).toInt(),
                weeklyActivity = buildWeeklyActivity()
            )
        }

    override suspend fun recordLearningTime(userId: String, courseId: String, seconds: Long): Result<Unit> {
        return try {
            val progress = localDataSource.getProgress(userId, courseId).first()
            if (progress != null) {
                val updated = progress.copy(
                    totalTimeSpentSeconds = progress.totalTimeSpentSeconds + seconds
                )
                localDataSource.saveProgress(updated)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildWeeklyActivity(): List<WeeklyActivity> {
        return listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").map { day ->
            WeeklyActivity(day, (0..120).random())
        }
    }
}
