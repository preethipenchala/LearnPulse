package com.learnpulse.presentation.home

import com.learnpulse.domain.model.Course
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

data class HomeState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val streakDays: Int = 0,
    val continueLearnCourse: Course? = null,
    val continueLearnProgress: Float = 0f,
    val trendingCourses: List<Course> = emptyList(),
    val recommendedCourses: List<Course> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null
) : UiState

sealed interface HomeIntent : UiIntent {
    data object LoadHome : HomeIntent
    data class SearchQueryChanged(val query: String) : HomeIntent
    data class CourseClicked(val courseId: String) : HomeIntent
    data object SearchSubmitted : HomeIntent
    data object RefreshRequested : HomeIntent
}

sealed interface HomeEffect : UiEffect {
    data class NavigateToCourse(val courseId: String) : HomeEffect
    data class NavigateToSearch(val query: String) : HomeEffect
}

class HomeViewModel(
    private val courseRepository: CourseRepository,
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository
) : BaseViewModel<HomeState, HomeIntent, HomeEffect>(HomeState()) {

    init {
        onIntent(HomeIntent.LoadHome)
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadHome -> loadHome()
            is HomeIntent.SearchQueryChanged -> updateState { copy(searchQuery = intent.query) }
            is HomeIntent.CourseClicked -> emitEffect(HomeEffect.NavigateToCourse(intent.courseId))
            is HomeIntent.SearchSubmitted -> emitEffect(HomeEffect.NavigateToSearch(state.value.searchQuery))
            is HomeIntent.RefreshRequested -> loadHome()
        }
    }

    private fun loadHome() {
        screenModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser().first()
                val trending = courseRepository.getTrendingCourses().first()
                val recommended = if (user != null) {
                    courseRepository.getRecommendedCourses(user.id).first()
                } else trending.take(5)

                val lastProgress = if (user != null) {
                    progressRepository.getAllProgress(user.id).first()
                        .maxByOrNull { it.totalTimeSpentSeconds }
                } else null

                val continueLearnCourse = lastProgress?.let {
                    courseRepository.getCourseById(it.courseId)
                }

                updateState {
                    copy(
                        isLoading = false,
                        userName = user?.name ?: "Learner",
                        streakDays = user?.streakDays ?: 0,
                        trendingCourses = trending,
                        recommendedCourses = recommended,
                        continueLearnCourse = continueLearnCourse,
                        continueLearnProgress = lastProgress?.overallProgress ?: 0f
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}
