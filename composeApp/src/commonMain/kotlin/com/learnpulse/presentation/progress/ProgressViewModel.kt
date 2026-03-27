package com.learnpulse.presentation.progress

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.model.LearningStats
import com.learnpulse.domain.model.UserProgress
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.ProgressRepository
import com.learnpulse.domain.repository.UserRepository
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProgressScreenState(
    val isLoading: Boolean = true,
    val stats: LearningStats? = null,
    val courseProgressList: List<Pair<Course, UserProgress>> = emptyList(),
    val error: String? = null
) : UiState

sealed interface ProgressIntent : UiIntent {
    data object LoadProgress : ProgressIntent
    data class CourseClicked(val courseId: String) : ProgressIntent
}

sealed interface ProgressEffect : UiEffect {
    data class NavigateToCourse(val courseId: String) : ProgressEffect
}

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) : BaseViewModel<ProgressScreenState, ProgressIntent, ProgressEffect>(ProgressScreenState()) {

    init {
        onIntent(ProgressIntent.LoadProgress)
    }

    override fun onIntent(intent: ProgressIntent) {
        when (intent) {
            is ProgressIntent.LoadProgress -> loadProgress()
            is ProgressIntent.CourseClicked -> emitEffect(ProgressEffect.NavigateToCourse(intent.courseId))
        }
    }

    private fun loadProgress() {
        screenModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser().first() ?: return@launch
                val stats = progressRepository.getLearningStats(user.id).first()
                val allProgress = progressRepository.getAllProgress(user.id).first()
                val courseProgressList = allProgress.mapNotNull { progress ->
                    val course = courseRepository.getCourseById(progress.courseId)
                    course?.let { Pair(it, progress) }
                }
                updateState {
                    copy(
                        isLoading = false,
                        stats = stats,
                        courseProgressList = courseProgressList
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}
