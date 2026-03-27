package com.learnpulse.domain.usecase

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class SearchCoursesUseCase(private val courseRepository: CourseRepository) {
    operator fun invoke(query: String, page: Int = 0): Flow<List<Course>> =
        courseRepository.searchCourses(query, page)
}
