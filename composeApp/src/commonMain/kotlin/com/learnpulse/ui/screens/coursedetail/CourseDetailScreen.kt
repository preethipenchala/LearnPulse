package com.learnpulse.ui.screens.coursedetail

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.learnpulse.domain.model.CourseSection
import com.learnpulse.domain.model.Lesson
import com.learnpulse.presentation.coursedetail.CourseDetailEffect
import com.learnpulse.presentation.coursedetail.CourseDetailIntent
import com.learnpulse.presentation.coursedetail.CourseDetailViewModel
import com.learnpulse.ui.components.*
import com.learnpulse.ui.screens.player.PlayerScreen
import com.learnpulse.ui.theme.LearnPulseTheme
import com.learnpulse.ui.util.toFixed

data class CourseDetailScreen(val courseId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<CourseDetailViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(courseId) {
            viewModel.onIntent(CourseDetailIntent.LoadCourse(courseId))
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is CourseDetailEffect.NavigateToPlayer -> navigator.push(PlayerScreen(effect.lesson, courseId))
                    is CourseDetailEffect.NavigateToLogin -> navigator.pop()
                    is CourseDetailEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(state.course?.title ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                state.course?.let { course ->
                    EnrollBar(
                        isEnrolled = state.isEnrolled,
                        isEnrolling = state.isEnrolling,
                        isFree = course.isFree,
                        price = course.price,
                        onEnroll = { viewModel.onIntent(CourseDetailIntent.EnrollClicked) }
                    )
                }
            }
        ) { paddingValues ->
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.onIntent(CourseDetailIntent.LoadCourse(courseId)) }
                )
                state.course != null -> {
                    CourseDetailContent(
                        state = state,
                        paddingValues = paddingValues,
                        onIntent = viewModel::onIntent
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseDetailContent(
    state: com.learnpulse.presentation.coursedetail.CourseDetailState,
    paddingValues: PaddingValues,
    onIntent: (CourseDetailIntent) -> Unit
) {
    val course = state.course ?: return
    val spacing = LearnPulseTheme.spacing

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            AsyncImage(
                model = course.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(240.dp),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Column(modifier = Modifier.padding(spacing.md)) {
                Text(course.title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(spacing.sm))
                Text(course.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(spacing.md))
                CourseMetaRow(course = course)
                Spacer(Modifier.height(spacing.md))
                InstructorCard(course.instructor)
            }
        }
        // Progress bar if enrolled
        state.progress?.let { progress ->
            item {
                Column(modifier = Modifier.padding(horizontal = spacing.md)) {
                    Text("Your Progress: ${(progress.overallProgress * 100).toInt()}%", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    LearnPulseProgressBar(progress = progress.overallProgress)
                    Spacer(Modifier.height(spacing.md))
                }
            }
        }
        item {
            Text("Curriculum", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = spacing.md))
        }
        state.sections.forEachIndexed { index, section ->
            item {
                SectionItem(
                    section = section,
                    isExpanded = state.expandedSectionIndices.contains(index),
                    onToggle = { onIntent(CourseDetailIntent.ToggleSection(index)) },
                    completedLessons = state.progress?.completedLessons ?: emptyList(),
                    onLessonClick = { lesson -> onIntent(CourseDetailIntent.LessonClicked(lesson)) }
                )
            }
        }
        if (state.reviews.isNotEmpty()) {
            item {
                Text("Reviews", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm))
            }
            items(state.reviews.take(3)) { review ->
                ReviewItem(review, modifier = Modifier.padding(horizontal = spacing.md, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun CourseMetaRow(course: com.learnpulse.domain.model.Course) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        RatingBar(course.rating)
        Text(course.rating.toFixed(1), style = MaterialTheme.typography.bodyMedium)
        Text("•")
        Text(formatDuration(course.totalDuration), style = MaterialTheme.typography.bodyMedium)
        Text("•")
        Text(course.difficulty.displayName(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InstructorCard(instructor: com.learnpulse.domain.model.Instructor) {
    Card(shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = instructor.avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(56.dp).padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(instructor.name, style = MaterialTheme.typography.titleMedium)
                Text(instructor.bio, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun SectionItem(
    section: CourseSection,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    completedLessons: List<String>,
    onLessonClick: (Lesson) -> Unit
) {
    Column {
        ListItem(
            headlineContent = { Text(section.title, style = MaterialTheme.typography.titleSmall) },
            supportingContent = { Text("${section.lessons.size} lessons") },
            trailingContent = {
                Icon(if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
            },
            modifier = Modifier.clickable(onClick = onToggle)
        )
        AnimatedVisibility(visible = isExpanded) {
            Column {
                section.lessons.forEach { lesson ->
                    LessonItem(
                        lesson = lesson,
                        isCompleted = completedLessons.contains(lesson.id),
                        onClick = { onLessonClick(lesson) }
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun LessonItem(lesson: Lesson, isCompleted: Boolean, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(lesson.title, style = MaterialTheme.typography.bodyMedium) },
        leadingContent = { LessonTypeIcon(lesson.type.name) },
        supportingContent = { Text(formatDuration(lesson.duration), style = MaterialTheme.typography.bodySmall) },
        trailingContent = {
            if (isCompleted) Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
            else if (lesson.isPreview) Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        modifier = Modifier.clickable(onClick = onClick).padding(start = 16.dp)
    )
}

@Composable
private fun ReviewItem(review: com.learnpulse.domain.model.CourseReview, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(review.userName, style = MaterialTheme.typography.titleSmall)
                RatingBar(review.rating.toDouble(), starSize = 14.dp)
            }
            Spacer(Modifier.height(4.dp))
            Text(review.comment, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun EnrollBar(
    isEnrolled: Boolean,
    isEnrolling: Boolean,
    isFree: Boolean,
    price: Double,
    onEnroll: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isEnrolled) {
                Text(
                    text = if (isFree) "Free" else "$${price.toFixed(2)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = onEnroll,
                enabled = !isEnrolling && !isEnrolled,
                modifier = Modifier.fillMaxWidth(if (isEnrolled) 1f else 0.6f)
            ) {
                if (isEnrolling) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (isEnrolled) "✓ Enrolled" else if (isFree) "Enroll Free" else "Buy Now")
                }
            }
        }
    }
}
