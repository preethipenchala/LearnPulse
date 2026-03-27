package com.learnpulse.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val title: String,
    val description: String,
    val instructor: Instructor,
    val thumbnailUrl: String,
    val category: CourseCategory,
    val difficulty: Difficulty,
    val rating: Double,
    val enrolledCount: Int,
    val totalDuration: Long, // seconds
    val lessons: List<Lesson>,
    val price: Double,
    val isFree: Boolean,
    val tags: List<String>
)

@Serializable
data class Instructor(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val bio: String,
    val courseCount: Int,
    val rating: Double = 0.0
)

@Serializable
enum class CourseCategory {
    PROGRAMMING, DESIGN, BUSINESS, DATA_SCIENCE, LANGUAGE, MATH;

    fun displayName(): String = when (this) {
        PROGRAMMING -> "Programming"
        DESIGN -> "Design"
        BUSINESS -> "Business"
        DATA_SCIENCE -> "Data Science"
        LANGUAGE -> "Language"
        MATH -> "Mathematics"
    }
}

@Serializable
enum class Difficulty {
    BEGINNER, INTERMEDIATE, ADVANCED;

    fun displayName(): String = when (this) {
        BEGINNER -> "Beginner"
        INTERMEDIATE -> "Intermediate"
        ADVANCED -> "Advanced"
    }
}

@Serializable
data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val type: LessonType,
    val duration: Long, // seconds
    val contentUrl: String,
    val order: Int,
    val isPreview: Boolean,
    val description: String = ""
)

@Serializable
enum class LessonType {
    VIDEO, TEXT, INTERACTIVE, QUIZ
}

@Serializable
data class Quiz(
    val id: String,
    val lessonId: String,
    val title: String,
    val questions: List<QuizQuestion>,
    val passingScore: Int,
    val timeLimit: Long? = null // seconds, null = untimed
)

@Serializable
data class QuizQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Serializable
data class UserProgress(
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
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val enrolledCourseIds: List<String> = emptyList(),
    val completedCourseIds: List<String> = emptyList(),
    val streakDays: Int = 0,
    val totalLearningTimeSeconds: Long = 0L,
    val certificates: List<Certificate> = emptyList(),
    val preferences: UserPreferences = UserPreferences()
)

@Serializable
data class UserPreferences(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val downloadOverWifiOnly: Boolean = true,
    val playbackSpeed: Float = 1.0f
)

@Serializable
data class Certificate(
    val id: String,
    val courseId: String,
    val courseTitle: String,
    val issuedAt: Long, // epoch millis
    val imageUrl: String
)

@Serializable
data class Note(
    val id: String,
    val userId: String,
    val courseId: String,
    val lessonId: String,
    val content: String,
    val timestampSeconds: Long = 0L, // video timestamp
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class Bookmark(
    val id: String,
    val userId: String,
    val courseId: String,
    val lessonId: String,
    val title: String,
    val createdAt: Long
)

@Serializable
data class DownloadedLesson(
    val lessonId: String,
    val courseId: String,
    val title: String,
    val localFilePath: String,
    val fileSizeBytes: Long,
    val downloadedAt: Long,
    val status: DownloadStatus
)

@Serializable
enum class DownloadStatus {
    QUEUED, IN_PROGRESS, COMPLETED, FAILED, PAUSED
}

@Serializable
data class CourseReview(
    val id: String,
    val courseId: String,
    val userId: String,
    val userName: String,
    val userAvatarUrl: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long
)

@Serializable
data class WeeklyActivity(
    val dayLabel: String,
    val minutesLearned: Int
)

@Serializable
data class LearningStats(
    val coursesEnrolled: Int,
    val coursesCompleted: Int,
    val streakDays: Int,
    val certificatesEarned: Int,
    val totalLearningMinutes: Int,
    val weeklyActivity: List<WeeklyActivity>
)

data class CourseSection(
    val title: String,
    val lessons: List<Lesson>
)
