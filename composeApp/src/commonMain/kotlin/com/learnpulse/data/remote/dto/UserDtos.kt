package com.learnpulse.data.remote.dto

import com.learnpulse.domain.model.*
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val enrolledCourseIds: List<String> = emptyList(),
    val completedCourseIds: List<String> = emptyList(),
    val streakDays: Int = 0,
    val totalLearningTimeSeconds: Long = 0L
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val user: UserDto,
    val token: String,
    val refreshToken: String
)

@Serializable
data class UserProgressDto(
    val userId: String,
    val courseId: String,
    val completedLessons: List<String>,
    val quizScores: Map<String, Int>,
    val lastAccessedLessonId: String?,
    val overallProgress: Float,
    val certificateEarned: Boolean,
    val streakDays: Int,
    val totalTimeSpentSeconds: Long = 0L
)

@Serializable
data class QuizDto(
    val id: String,
    val lessonId: String,
    val title: String,
    val questions: List<QuizQuestionDto>,
    val passingScore: Int,
    val timeLimit: Long? = null
)

@Serializable
data class QuizQuestionDto(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Serializable
data class GenerateQuizRequest(
    val topic: String,
    val difficulty: String,
    val questionCount: Int
)

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    avatarUrl = avatarUrl,
    enrolledCourseIds = enrolledCourseIds,
    completedCourseIds = completedCourseIds,
    streakDays = streakDays,
    totalLearningTimeSeconds = totalLearningTimeSeconds
)

fun UserProgressDto.toDomain(): UserProgress = UserProgress(
    userId = userId,
    courseId = courseId,
    completedLessons = completedLessons,
    quizScores = quizScores,
    lastAccessedLessonId = lastAccessedLessonId,
    overallProgress = overallProgress,
    certificateEarned = certificateEarned,
    streakDays = streakDays,
    totalTimeSpentSeconds = totalTimeSpentSeconds
)

fun QuizDto.toDomain(): Quiz = Quiz(
    id = id,
    lessonId = lessonId,
    title = title,
    questions = questions.map { it.toDomain() },
    passingScore = passingScore,
    timeLimit = timeLimit
)

fun QuizQuestionDto.toDomain(): QuizQuestion = QuizQuestion(
    id = id,
    text = text,
    options = options,
    correctIndex = correctIndex,
    explanation = explanation
)
