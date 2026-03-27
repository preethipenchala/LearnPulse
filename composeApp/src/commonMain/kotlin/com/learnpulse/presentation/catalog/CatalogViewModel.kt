package com.learnpulse.presentation.catalog

import com.learnpulse.domain.model.Course
import com.learnpulse.domain.model.CourseCategory
import com.learnpulse.domain.model.Difficulty
import com.learnpulse.domain.repository.CourseRepository
import com.learnpulse.domain.repository.CourseSortOption
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CatalogState(
    val isLoading: Boolean = true,
    val courses: List<Course> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: CourseCategory? = null,
    val selectedDifficulty: Difficulty? = null,
    val sortBy: CourseSortOption = CourseSortOption.POPULARITY,
    val isGridView: Boolean = true,
    val currentPage: Int = 0,
    val hasMorePages: Boolean = true,
    val error: String? = null
) : UiState

sealed interface CatalogIntent : UiIntent {
    data object LoadCourses : CatalogIntent
    data class SearchQueryChanged(val query: String) : CatalogIntent
    data class CategorySelected(val category: CourseCategory?) : CatalogIntent
    data class DifficultySelected(val difficulty: Difficulty?) : CatalogIntent
    data class SortByChanged(val sortBy: CourseSortOption) : CatalogIntent
    data object ToggleViewMode : CatalogIntent
    data object LoadNextPage : CatalogIntent
    data class CourseClicked(val courseId: String) : CatalogIntent
}

sealed interface CatalogEffect : UiEffect {
    data class NavigateToCourse(val courseId: String) : CatalogEffect
}

class CatalogViewModel(
    private val courseRepository: CourseRepository
) : BaseViewModel<CatalogState, CatalogIntent, CatalogEffect>(CatalogState()) {

    private var searchJob: Job? = null

    init {
        onIntent(CatalogIntent.LoadCourses)
    }

    override fun onIntent(intent: CatalogIntent) {
        when (intent) {
            is CatalogIntent.LoadCourses -> loadCourses(reset = true)
            is CatalogIntent.SearchQueryChanged -> {
                updateState { copy(searchQuery = intent.query) }
                searchJob?.cancel()
                searchJob = screenModelScope.launch {
                    delay(300)
                    loadCourses(reset = true)
                }
            }
            is CatalogIntent.CategorySelected -> {
                updateState { copy(selectedCategory = intent.category) }
                loadCourses(reset = true)
            }
            is CatalogIntent.DifficultySelected -> {
                updateState { copy(selectedDifficulty = intent.difficulty) }
                loadCourses(reset = true)
            }
            is CatalogIntent.SortByChanged -> {
                updateState { copy(sortBy = intent.sortBy) }
                loadCourses(reset = true)
            }
            is CatalogIntent.ToggleViewMode -> updateState { copy(isGridView = !isGridView) }
            is CatalogIntent.LoadNextPage -> loadCourses(reset = false)
            is CatalogIntent.CourseClicked -> emitEffect(CatalogEffect.NavigateToCourse(intent.courseId))
        }
    }

    private fun loadCourses(reset: Boolean) {
        screenModelScope.launch {
            val page = if (reset) 0 else state.value.currentPage + 1
            updateState { copy(isLoading = true, error = null, currentPage = page) }
            try {
                val st = state.value
                val courses = if (st.searchQuery.isNotBlank()) {
                    courseRepository.searchCourses(st.searchQuery, page).first()
                } else {
                    courseRepository.getCourses(
                        category = st.selectedCategory,
                        difficulty = st.selectedDifficulty,
                        sortBy = st.sortBy,
                        page = page
                    ).first()
                }
                updateState {
                    copy(
                        isLoading = false,
                        courses = if (reset) courses else this.courses + courses,
                        hasMorePages = courses.size >= 20
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}
