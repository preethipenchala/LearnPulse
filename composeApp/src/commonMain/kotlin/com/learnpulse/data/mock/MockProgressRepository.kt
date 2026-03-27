package com.learnpulse.data.mock

import com.learnpulse.domain.model.LearningStats
import com.learnpulse.domain.model.UserProgress
import com.learnpulse.domain.model.WeeklyActivity
import com.learnpulse.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MockProgressRepository : ProgressRepository {

    private val _progressList = MutableStateFlow(MockDataSource.progressList.toMutableList())

    override fun getProgress(userId: String, courseId: String): Flow<UserProgress?> =
        _progressList.map { list -> list.find { it.userId == userId && it.courseId == courseId } }

    override fun getAllProgress(userId: String): Flow<List<UserProgress>> =
        _progressList.map { list -> list.filter { it.userId == userId } }

    override suspend fun markLessonComplete(userId: String, courseId: String, lessonId: String): Result<Unit> {
        val current = _progressList.value
        val index = current.indexOfFirst { it.userId == userId && it.courseId == courseId }
        if (index >= 0) {
            val updated = current[index].let {
                val completedLessons = (it.completedLessons + lessonId).distinct()
                val course = MockDataSource.courses.find { c -> c.id == courseId }
                val totalLessons = course?.lessons?.size?.takeIf { s -> s > 0 } ?: 1
                it.copy(
                    completedLessons = completedLessons,
                    overallProgress = completedLessons.size.toFloat() / totalLessons
                )
            }
            val newList = current.toMutableList().also { it[index] = updated }
            _progressList.value = newList
        } else {
            val course = MockDataSource.courses.find { it.id == courseId }
            val totalLessons = course?.lessons?.size?.takeIf { it > 0 } ?: 1
            val newProgress = UserProgress(
                userId = userId,
                courseId = courseId,
                completedLessons = listOf(lessonId),
                quizScores = emptyMap(),
                lastAccessedLessonId = lessonId,
                overallProgress = 1f / totalLessons,
                certificateEarned = false,
                streakDays = 1,
                totalTimeSpentSeconds = 0L
            )
            _progressList.value = (current + newProgress).toMutableList()
        }
        return Result.success(Unit)
    }

    override suspend fun saveQuizScore(userId: String, courseId: String, lessonId: String, score: Int): Result<Unit> {
        val current = _progressList.value
        val index = current.indexOfFirst { it.userId == userId && it.courseId == courseId }
        if (index >= 0) {
            val updated = current[index].copy(
                quizScores = current[index].quizScores + (lessonId to score)
            )
            _progressList.value = current.toMutableList().also { it[index] = updated }
        }
        return Result.success(Unit)
    }

    override suspend fun updateLastAccessed(userId: String, courseId: String, lessonId: String): Result<Unit> {
        val current = _progressList.value
        val index = current.indexOfFirst { it.userId == userId && it.courseId == courseId }
        if (index >= 0) {
            val updated = current[index].copy(lastAccessedLessonId = lessonId)
            _progressList.value = current.toMutableList().also { it[index] = updated }
        }
        return Result.success(Unit)
    }

    override fun getLearningStats(userId: String): Flow<LearningStats> =
        _progressList.map { list ->
            val userProgress = list.filter { it.userId == userId }
            val completed = userProgress.count { it.certificateEarned }
            val totalSeconds = userProgress.sumOf { it.totalTimeSpentSeconds }
            val streak = userProgress.maxOfOrNull { it.streakDays } ?: 0
            LearningStats(
                coursesEnrolled = userProgress.size,
                coursesCompleted = completed,
                streakDays = streak,
                certificatesEarned = completed,
                totalLearningMinutes = (totalSeconds / 60).toInt(),
                weeklyActivity = mockWeeklyActivity()
            )
        }

    override suspend fun recordLearningTime(userId: String, courseId: String, seconds: Long): Result<Unit> {
        val current = _progressList.value
        val index = current.indexOfFirst { it.userId == userId && it.courseId == courseId }
        if (index >= 0) {
            val updated = current[index].copy(
                totalTimeSpentSeconds = current[index].totalTimeSpentSeconds + seconds
            )
            _progressList.value = current.toMutableList().also { it[index] = updated }
        }
        return Result.success(Unit)
    }

    private fun mockWeeklyActivity() = listOf(
        WeeklyActivity("Mon", 45),
        WeeklyActivity("Tue", 90),
        WeeklyActivity("Wed", 30),
        WeeklyActivity("Thu", 120),
        WeeklyActivity("Fri", 60),
        WeeklyActivity("Sat", 15),
        WeeklyActivity("Sun", 75)
    )
}
