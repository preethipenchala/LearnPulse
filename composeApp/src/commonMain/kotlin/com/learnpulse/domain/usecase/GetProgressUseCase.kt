package com.learnpulse.domain.usecase

import com.learnpulse.domain.model.LearningStats
import com.learnpulse.domain.model.UserProgress
import com.learnpulse.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow

class GetProgressUseCase(private val progressRepository: ProgressRepository) {
    fun getCourseProgress(userId: String, courseId: String): Flow<UserProgress?> =
        progressRepository.getProgress(userId, courseId)

    fun getAllProgress(userId: String): Flow<List<UserProgress>> =
        progressRepository.getAllProgress(userId)

    fun getLearningStats(userId: String): Flow<LearningStats> =
        progressRepository.getLearningStats(userId)
}
