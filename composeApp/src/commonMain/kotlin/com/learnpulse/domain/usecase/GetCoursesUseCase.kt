package com.learnpulse.domain.usecase

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.model.CourseCategory
import com.learnpulse.domain.model.Difficulty
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.CourseSortOption
import kotlinx.coroutines.flow.Flow

class GetCoursesUseCase(private val courseRepository: CourseRepository) {
    operator fun invoke(
        category: CourseCategory? = null,
        difficulty: Difficulty? = null,
        sortBy: CourseSortOption = CourseSortOption.POPULARITY,
        page: Int = 0
    ): Flow<List<Course>> = courseRepository.getCourses(
        category = category,
        difficulty = difficulty,
        sortBy = sortBy,
        page = page
    )
}
