package com.learnpulse.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.domain.model.Course
import com.learnpulse.presentation.home.HomeEffect
import com.learnpulse.presentation.home.HomeIntent
import com.learnpulse.presentation.home.HomeViewModel
import com.learnpulse.ui.components.CourseCard
import com.learnpulse.ui.components.CourseCardCompact
import com.learnpulse.ui.components.ErrorState
import com.learnpulse.ui.components.LoadingIndicator
import com.learnpulse.ui.components.StreakBadge
import com.learnpulse.ui.components.formatDuration
import com.learnpulse.ui.screens.catalog.CatalogScreen
import com.learnpulse.ui.screens.coursedetail.CourseDetailScreen
import com.learnpulse.ui.theme.LearnPulseTheme

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<HomeViewModel>()
        val state by viewModel.state.collectAsState()
        val spacing = LearnPulseTheme.spacing

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is HomeEffect.NavigateToCourse -> navigator.push(CourseDetailScreen(effect.courseId))
                    is HomeEffect.NavigateToSearch -> navigator.push(CatalogScreen(initialQuery = effect.query))
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "Hello, ${state.userName} 👋",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "What will you learn today?",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        if (state.streakDays > 0) {
                            StreakBadge(state.streakDays, modifier = Modifier.padding(end = 8.dp))
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                )
            }
        ) { paddingValues ->
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.onIntent(HomeIntent.RefreshRequested) }
                )
                else -> HomeContent(
                    state = state,
                    paddingValues = paddingValues,
                    onIntent = viewModel::onIntent
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: com.learnpulse.presentation.home.HomeState,
    paddingValues: PaddingValues,
    onIntent: (HomeIntent) -> Unit
) {
    val spacing = LearnPulseTheme.spacing
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues)
    ) {
        // Search bar
        item {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(HomeIntent.SearchQueryChanged(it)) },
                onSearch = { onIntent(HomeIntent.SearchSubmitted) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.md, vertical = spacing.sm)
            )
        }

        // Continue Learning
        state.continueLearnCourse?.let { course ->
            item {
                ContinueLearningCard(
                    course = course,
                    progress = state.continueLearnProgress,
                    onClick = { onIntent(HomeIntent.CourseClicked(course.id)) },
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm)
                )
            }
        }

        // Trending
        if (state.trendingCourses.isNotEmpty()) {
            item {
                SectionHeader("🔥 Trending Now", modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm))
            }
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    items(state.trendingCourses) { course ->
                        CourseCardCompact(
                            course = course,
                            onClick = { onIntent(HomeIntent.CourseClicked(course.id)) }
                        )
                    }
                }
            }
        }

        // Recommended
        if (state.recommendedCourses.isNotEmpty()) {
            item {
                SectionHeader("✨ Recommended For You", modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm))
            }
            items(state.recommendedCourses.take(5)) { course ->
                CourseCard(
                    course = course,
                    onClick = { onIntent(HomeIntent.CourseClicked(course.id)) },
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search courses, topics...") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        )
    )
}

@Composable
private fun ContinueLearningCard(
    course: Course,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Continue Learning", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(8.dp))
            Text(course.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Text(course.instructor.name, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            com.learnpulse.ui.components.LearnPulseProgressBar(progress = progress)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}% completed",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier
    )
}
