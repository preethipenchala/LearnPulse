package com.learnpulse.data.remote.dto

import com.learnpulse.domain.model.*
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    val id: String,
    val title: String,
    val description: String,
    val instructor: InstructorDto,
    val thumbnailUrl: String,
    val category: String,
    val difficulty: String,
    val rating: Double,
    val enrolledCount: Int,
    val totalDuration: Long,
    val lessons: List<LessonDto> = emptyList(),
    val price: Double,
    val isFree: Boolean,
    val tags: List<String> = emptyList()
)

@Serializable
data class InstructorDto(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val bio: String,
    val courseCount: Int,
    val rating: Double = 0.0
)

@Serializable
data class LessonDto(
    val id: String,
    val courseId: String,
    val title: String,
    val type: String,
    val duration: Long,
    val contentUrl: String,
    val order: Int,
    val isPreview: Boolean,
    val description: String = ""
)

@Serializable
data class CoursesResponse(
    val courses: List<CourseDto>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CourseReviewDto(
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
data class EnrollRequest(
    val userId: String,
    val courseId: String
)

fun CourseDto.toDomain(): Course = Course(
    id = id,
    title = title,
    description = description,
    instructor = instructor.toDomain(),
    thumbnailUrl = thumbnailUrl,
    category = CourseCategory.entries.find { it.name == category } ?: CourseCategory.PROGRAMMING,
    difficulty = Difficulty.entries.find { it.name == difficulty } ?: Difficulty.BEGINNER,
    rating = rating,
    enrolledCount = enrolledCount,
    totalDuration = totalDuration,
    lessons = lessons.map { it.toDomain() },
    price = price,
    isFree = isFree,
    tags = tags
)

fun InstructorDto.toDomain(): Instructor = Instructor(
    id = id,
    name = name,
    avatarUrl = avatarUrl,
    bio = bio,
    courseCount = courseCount,
    rating = rating
)

fun LessonDto.toDomain(): Lesson = Lesson(
    id = id,
    courseId = courseId,
    title = title,
    type = LessonType.entries.find { it.name == type } ?: LessonType.VIDEO,
    duration = duration,
    contentUrl = contentUrl,
    order = order,
    isPreview = isPreview,
    description = description
)

fun CourseReviewDto.toDomain(): CourseReview = CourseReview(
    id = id,
    courseId = courseId,
    userId = userId,
    userName = userName,
    userAvatarUrl = userAvatarUrl,
    rating = rating,
    comment = comment,
    createdAt = createdAt
)
