package com.learnpulse.presentation.coursedetail

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.model.CourseReview
import com.learnpulse.domain.model.CourseSection
import com.learnpulse.domain.model.Lesson
import com.learnpulse.domain.model.UserProgress
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.ProgressRepository
import com.learnpulse.domain.repository.UserRepository
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CourseDetailState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val sections: List<CourseSection> = emptyList(),
    val reviews: List<CourseReview> = emptyList(),
    val progress: UserProgress? = null,
    val isEnrolled: Boolean = false,
    val expandedSectionIndices: Set<Int> = setOf(0),
    val isEnrolling: Boolean = false,
    val error: String? = null
) : UiState

sealed interface CourseDetailIntent : UiIntent {
    data class LoadCourse(val courseId: String) : CourseDetailIntent
    data class ToggleSection(val index: Int) : CourseDetailIntent
    data object EnrollClicked : CourseDetailIntent
    data class LessonClicked(val lesson: Lesson) : CourseDetailIntent
}

sealed interface CourseDetailEffect : UiEffect {
    data class NavigateToPlayer(val lesson: Lesson) : CourseDetailEffect
    data object NavigateToLogin : CourseDetailEffect
    data class ShowMessage(val message: String) : CourseDetailEffect
}

class CourseDetailViewModel(
    private val courseRepository: CourseRepository,
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository
) : BaseViewModel<CourseDetailState, CourseDetailIntent, CourseDetailEffect>(CourseDetailState()) {

    private var courseId: String = ""

    override fun onIntent(intent: CourseDetailIntent) {
        when (intent) {
            is CourseDetailIntent.LoadCourse -> {
                courseId = intent.courseId
                loadCourse(intent.courseId)
            }
            is CourseDetailIntent.ToggleSection -> {
                updateState {
                    val newExpanded = if (expandedSectionIndices.contains(intent.index)) {
                        expandedSectionIndices - intent.index
                    } else {
                        expandedSectionIndices + intent.index
                    }
                    copy(expandedSectionIndices = newExpanded)
                }
            }
            is CourseDetailIntent.EnrollClicked -> enrollInCourse()
            is CourseDetailIntent.LessonClicked -> {
                val lesson = intent.lesson
                if (state.value.isEnrolled || lesson.isPreview) {
                    emitEffect(CourseDetailEffect.NavigateToPlayer(lesson))
                } else {
                    emitEffect(CourseDetailEffect.ShowMessage("Enroll to access this lesson"))
                }
            }
        }
    }

    private fun loadCourse(courseId: String) {
        screenModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            try {
                val course = courseRepository.getCourseById(courseId)
                val reviews = courseRepository.getCourseReviews(courseId).first()
                val user = userRepository.getCurrentUser().first()
                val progress = user?.let {
                    progressRepository.getProgress(it.id, courseId).first()
                }
                val sections = buildSections(course?.lessons ?: emptyList())
                val isEnrolled = user?.enrolledCourseIds?.contains(courseId) == true

                updateState {
                    copy(
                        isLoading = false,
                        course = course,
                        sections = sections,
                        reviews = reviews,
                        progress = progress,
                        isEnrolled = isEnrolled
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun enrollInCourse() {
        screenModelScope.launch {
            val user = userRepository.getCurrentUser().first()
            if (user == null) {
                emitEffect(CourseDetailEffect.NavigateToLogin)
                return@launch
            }
            updateState { copy(isEnrolling = true) }
            val result = courseRepository.enrollInCourse(user.id, courseId)
            updateState { copy(isEnrolling = false) }
            if (result.isSuccess) {
                updateState { copy(isEnrolled = true) }
                emitEffect(CourseDetailEffect.ShowMessage("Successfully enrolled!"))
            } else {
                emitEffect(CourseDetailEffect.ShowMessage("Failed to enroll. Please try again."))
            }
        }
    }

    private fun buildSections(lessons: List<Lesson>): List<CourseSection> {
        if (lessons.isEmpty()) return emptyList()
        // Group lessons into chunks of ~5 as sections
        return lessons.chunked(5).mapIndexed { index, sectionLessons ->
            CourseSection(
                title = "Section ${index + 1}",
                lessons = sectionLessons
            )
        }
    }
}
