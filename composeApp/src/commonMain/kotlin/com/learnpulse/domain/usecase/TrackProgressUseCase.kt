package com.learnpulse.domain.usecase

import com.learnpulse.domain.repository.ProgressRepository

class TrackProgressUseCase(private val progressRepository: ProgressRepository) {
    suspend fun markLessonComplete(userId: String, courseId: String, lessonId: String): Result<Unit> =
        progressRepository.markLessonComplete(userId, courseId, lessonId)

    suspend fun updateLastAccessed(userId: String, courseId: String, lessonId: String): Result<Unit> =
        progressRepository.updateLastAccessed(userId, courseId, lessonId)

    suspend fun recordLearningTime(userId: String, courseId: String, seconds: Long): Result<Unit> =
        progressRepository.recordLearningTime(userId, courseId, seconds)
}
