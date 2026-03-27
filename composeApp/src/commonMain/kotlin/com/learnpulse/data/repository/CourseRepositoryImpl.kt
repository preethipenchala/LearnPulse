package com.learnpulse.data.repository

import com.learnpulse.data.local.LocalDataSource
import com.learnpulse.data.remote.api.LearnPulseApi
import com.learnpulse.data.remote.dto.EnrollRequest
import com.learnpulse.data.remote.dto.toDomain
import com.learnpulse.domain.model.Course
import com.learnpulse.domain.model.CourseCategory
import com.learnpulse.domain.model.CourseReview
import com.learnpulse.domain.model.Difficulty
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.CourseSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class CourseRepositoryImpl(
    private val api: LearnPulseApi,
    private val localDataSource: LocalDataSource
) : CourseRepository {

    override fun getCourses(
        category: CourseCategory?,
        difficulty: Difficulty?,
        sortBy: CourseSortOption,
        page: Int,
        pageSize: Int
    ): Flow<List<Course>> = flow {
        // Emit cached data first
        val cached = if (category != null) {
            localDataSource.getCachedCoursesByCategory(category.name).first()
        } else {
            localDataSource.getCachedCourses().first()
        }
        if (cached.isNotEmpty()) emit(cached)

        // Fetch from network
        try {
            val response = api.getCourses(
                category = category?.name,
                difficulty = difficulty?.name,
                sortBy = sortBy.name.lowercase(),
                page = page,
                pageSize = pageSize
            )
            val courses = response.courses.map { it.toDomain() }
            courses.forEach { localDataSource.cacheCourse(it) }
            emit(courses)
        } catch (e: Exception) {
            if (cached.isEmpty()) emit(emptyList())
        }
    }

    override fun searchCourses(query: String, page: Int): Flow<List<Course>> = flow {
        try {
            val response = api.searchCourses(query, page)
            emit(response.courses.map { it.toDomain() })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getFeaturedCourses(): Flow<List<Course>> = flow {
        try {
            val courses = api.getFeaturedCourses().map { it.toDomain() }
            courses.forEach { localDataSource.cacheCourse(it) }
            emit(courses)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getTrendingCourses(): Flow<List<Course>> = flow {
        try {
            val courses = api.getTrendingCourses().map { it.toDomain() }
            emit(courses)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getRecommendedCourses(userId: String): Flow<List<Course>> = flow {
        try {
            val courses = api.getRecommendedCourses(userId).map { it.toDomain() }
            emit(courses)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getCourseById(courseId: String): Course? {
        return try {
            val course = api.getCourseById(courseId).toDomain()
            localDataSource.cacheCourse(course)
            course
        } catch (e: Exception) {
            null
        }
    }

    override fun getCourseReviews(courseId: String): Flow<List<CourseReview>> = flow {
        try {
            val reviews = api.getCourseReviews(courseId).map { it.toDomain() }
            emit(reviews)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun enrollInCourse(userId: String, courseId: String): Result<Unit> {
        return try {
            api.enrollInCourse(EnrollRequest(userId, courseId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getEnrolledCourses(userId: String): Flow<List<Course>> = flow {
        try {
            val courses = api.getEnrolledCourses(userId).map { it.toDomain() }
            emit(courses)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
