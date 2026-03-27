package com.learnpulse.ui.screens.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.domain.model.CourseCategory
import com.learnpulse.domain.model.Difficulty
import com.learnpulse.domain.repository.CourseSortOption
import com.learnpulse.presentation.catalog.CatalogEffect
import com.learnpulse.presentation.catalog.CatalogIntent
import com.learnpulse.presentation.catalog.CatalogViewModel
import com.learnpulse.ui.components.CategoryChip
import com.learnpulse.ui.components.CourseCard
import com.learnpulse.ui.components.ErrorState
import com.learnpulse.ui.components.LoadingIndicator
import com.learnpulse.ui.screens.coursedetail.CourseDetailScreen
import com.learnpulse.ui.theme.LearnPulseTheme

data class CatalogScreen(val initialQuery: String = "") : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<CatalogViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            if (initialQuery.isNotBlank()) {
                viewModel.onIntent(CatalogIntent.SearchQueryChanged(initialQuery))
            }
            viewModel.effect.collect { effect ->
                when (effect) {
                    is CatalogEffect.NavigateToCourse -> navigator.push(CourseDetailScreen(effect.courseId))
                }
            }
        }

        Scaffold(
            topBar = {
                Column {
                    TopAppBar(title = { Text("Catalog") })
                    CatalogSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { viewModel.onIntent(CatalogIntent.SearchQueryChanged(it)) }
                    )
                    FilterRow(
                        selectedCategory = state.selectedCategory,
                        selectedDifficulty = state.selectedDifficulty,
                        sortBy = state.sortBy,
                        isGridView = state.isGridView,
                        onCategorySelected = { viewModel.onIntent(CatalogIntent.CategorySelected(it)) },
                        onDifficultySelected = { viewModel.onIntent(CatalogIntent.DifficultySelected(it)) },
                        onSortByChanged = { viewModel.onIntent(CatalogIntent.SortByChanged(it)) },
                        onToggleView = { viewModel.onIntent(CatalogIntent.ToggleViewMode) }
                    )
                }
            }
        ) { paddingValues ->
            when {
                state.isLoading && state.courses.isEmpty() -> LoadingIndicator()
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.onIntent(CatalogIntent.LoadCourses) }
                )
                else -> {
                    if (state.isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.courses) { course ->
                                CourseCard(
                                    course = course,
                                    onClick = { viewModel.onIntent(CatalogIntent.CourseClicked(course.id)) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(paddingValues),
                            contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.courses) { course ->
                                CourseCard(
                                    course = course,
                                    onClick = { viewModel.onIntent(CatalogIntent.CourseClicked(course.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search courses...") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true
    )
}

@Composable
private fun FilterRow(
    selectedCategory: CourseCategory?,
    selectedDifficulty: Difficulty?,
    sortBy: CourseSortOption,
    isGridView: Boolean,
    onCategorySelected: (CourseCategory?) -> Unit,
    onDifficultySelected: (Difficulty?) -> Unit,
    onSortByChanged: (CourseSortOption) -> Unit,
    onToggleView: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CategoryChip(
                label = "All",
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) }
            )
        }
        items(CourseCategory.entries) { category ->
            CategoryChip(
                label = category.displayName(),
                selected = selectedCategory == category,
                onClick = { onCategorySelected(if (selectedCategory == category) null else category) }
            )
        }
        item { Spacer(Modifier.width(8.dp)) }
        items(Difficulty.entries) { difficulty ->
            CategoryChip(
                label = difficulty.displayName(),
                selected = selectedDifficulty == difficulty,
                onClick = { onDifficultySelected(if (selectedDifficulty == difficulty) null else difficulty) },
                color = when (difficulty) {
                    Difficulty.BEGINNER -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    Difficulty.INTERMEDIATE -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                    Difficulty.ADVANCED -> androidx.compose.ui.graphics.Color(0xFFEF5350)
                }
            )
        }
        item {
            IconButton(onClick = onToggleView) {
                Icon(if (isGridView) Icons.Default.List else Icons.Default.GridView, null)
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}
