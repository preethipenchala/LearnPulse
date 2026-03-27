package com.learnpulse.domain.usecase

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.repository.CourseRepository

class GetCourseDetailUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseId: String): Course? =
        courseRepository.getCourseById(courseId)
}
