package com.learnpulse.data.mock

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.model.CourseCategory
import com.learnpulse.domain.model.CourseReview
import com.learnpulse.domain.model.Difficulty
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.CourseSortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MockCourseRepository : CourseRepository {

    private val enrolledIds = MutableStateFlow(MockDataSource.currentUser.enrolledCourseIds.toMutableSet())

    override fun getCourses(
        category: CourseCategory?,
        difficulty: Difficulty?,
        sortBy: CourseSortOption,
        page: Int,
        pageSize: Int
    ): Flow<List<Course>> = flow {
        var result = MockDataSource.courses

        if (category != null) result = result.filter { it.category == category }
        if (difficulty != null) result = result.filter { it.difficulty == difficulty }

        result = when (sortBy) {
            CourseSortOption.RATING -> result.sortedByDescending { it.rating }
            CourseSortOption.POPULARITY -> result.sortedByDescending { it.enrolledCount }
            CourseSortOption.PRICE_LOW -> result.sortedBy { it.price }
            CourseSortOption.PRICE_HIGH -> result.sortedByDescending { it.price }
            CourseSortOption.NEWEST -> result // mock order = newest
        }

        emit(result.drop(page * pageSize).take(pageSize).ifEmpty { result })
    }

    override fun searchCourses(query: String, page: Int): Flow<List<Course>> = flow {
        val q = query.lowercase()
        val results = MockDataSource.courses.filter { course ->
            course.title.lowercase().contains(q) ||
                course.description.lowercase().contains(q) ||
                course.tags.any { it.contains(q) } ||
                course.instructor.name.lowercase().contains(q) ||
                course.category.displayName().lowercase().contains(q)
        }
        emit(results)
    }

    override fun getFeaturedCourses(): Flow<List<Course>> = flow {
        emit(MockDataSource.courses.filter { it.rating >= 4.8 }.take(5))
    }

    override fun getTrendingCourses(): Flow<List<Course>> = flow {
        emit(MockDataSource.courses.sortedByDescending { it.enrolledCount }.take(5))
    }

    override fun getRecommendedCourses(userId: String): Flow<List<Course>> = flow {
        // Recommend free courses and high-rated courses not yet enrolled
        val enrolled = enrolledIds.value
        val recommended = MockDataSource.courses
            .filter { it.id !in enrolled }
            .sortedByDescending { it.rating }
            .take(4)
        emit(recommended)
    }

    override suspend fun getCourseById(courseId: String): Course? =
        MockDataSource.courses.find { it.id == courseId }

    override fun getCourseReviews(courseId: String): Flow<List<CourseReview>> = flow {
        emit(MockDataSource.reviews[courseId] ?: emptyList())
    }

    override suspend fun enrollInCourse(userId: String, courseId: String): Result<Unit> {
        enrolledIds.value = (enrolledIds.value + courseId).toMutableSet()
        return Result.success(Unit)
    }

    override fun getEnrolledCourses(userId: String): Flow<List<Course>> =
        enrolledIds.map { ids ->
            MockDataSource.courses.filter { it.id in ids }
        }
}
