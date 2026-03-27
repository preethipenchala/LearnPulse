package com.learnpulse.domain.repository

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.model.CourseCategory
import com.learnpulse.domain.model.CourseReview
import com.learnpulse.domain.model.Difficulty
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(
        category: CourseCategory? = null,
        difficulty: Difficulty? = null,
        sortBy: CourseSortOption = CourseSortOption.POPULARITY,
        page: Int = 0,
        pageSize: Int = 20
    ): Flow<List<Course>>

    fun searchCourses(query: String, page: Int = 0): Flow<List<Course>>

    fun getFeaturedCourses(): Flow<List<Course>>

    fun getTrendingCourses(): Flow<List<Course>>

    fun getRecommendedCourses(userId: String): Flow<List<Course>>

    suspend fun getCourseById(courseId: String): Course?

    fun getCourseReviews(courseId: String): Flow<List<CourseReview>>

    suspend fun enrollInCourse(userId: String, courseId: String): Result<Unit>

    fun getEnrolledCourses(userId: String): Flow<List<Course>>
}

enum class CourseSortOption {
    POPULARITY, RATING, NEWEST, PRICE_LOW, PRICE_HIGH
}
