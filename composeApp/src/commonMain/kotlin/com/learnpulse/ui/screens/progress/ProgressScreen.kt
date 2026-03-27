package com.learnpulse.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.domain.model.LearningStats
import com.learnpulse.domain.model.UserProgress
import com.learnpulse.presentation.progress.ProgressEffect
import com.learnpulse.presentation.progress.ProgressIntent
import com.learnpulse.presentation.progress.ProgressViewModel
import com.learnpulse.ui.components.*
import com.learnpulse.ui.screens.coursedetail.CourseDetailScreen
import com.learnpulse.ui.theme.LearnPulsePrimary

class ProgressScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<ProgressViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ProgressEffect.NavigateToCourse -> navigator.push(CourseDetailScreen(effect.courseId))
                }
            }
        }

        Scaffold(topBar = { TopAppBar(title = { Text("My Progress") }) }) { paddingValues ->
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(message = state.error!!, onRetry = { viewModel.onIntent(ProgressIntent.LoadProgress) })
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.stats?.let { stats ->
                        item { StatsOverview(stats) }
                        item { WeeklyActivityChart(stats.weeklyActivity) }
                    }
                    if (state.courseProgressList.isNotEmpty()) {
                        item { Text("Course Progress", style = MaterialTheme.typography.titleLarge) }
                        items(state.courseProgressList) { (course, progress) ->
                            CourseProgressItem(
                                courseTitle = course.title,
                                progress = progress,
                                onClick = { viewModel.onIntent(ProgressIntent.CourseClicked(course.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsOverview(stats: LearningStats) {
    Column {
        Text("Overview", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(value = "${stats.coursesEnrolled}", label = "Enrolled", modifier = Modifier.weight(1f))
            StatCard(value = "${stats.coursesCompleted}", label = "Completed", modifier = Modifier.weight(1f), color = Color(0xFF4CAF50))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(value = "${stats.streakDays}🔥", label = "Day Streak", modifier = Modifier.weight(1f), color = Color(0xFFFF9800))
            StatCard(value = "${stats.certificatesEarned}", label = "Certificates", modifier = Modifier.weight(1f), color = Color(0xFFFF6584))
        }
    }
}

@Composable
private fun WeeklyActivityChart(activity: List<com.learnpulse.domain.model.WeeklyActivity>) {
    if (activity.isEmpty()) return
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Weekly Activity", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            val maxMinutes = activity.maxOf { it.minutesLearned }.coerceAtLeast(1)
            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                val barWidth = size.width / (activity.size * 2)
                activity.forEachIndexed { index, day ->
                    val barHeight = (day.minutesLearned.toFloat() / maxMinutes) * size.height
                    val x = index * (size.width / activity.size) + barWidth / 2
                    drawRect(
                        color = LearnPulsePrimary,
                        topLeft = Offset(x, size.height - barHeight),
                        size = Size(barWidth, barHeight)
                    )
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                activity.forEach { day ->
                    Text(day.dayLabel, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun CourseProgressItem(courseTitle: String, progress: UserProgress, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(courseTitle, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                if (progress.certificateEarned) Text("🏆", style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(8.dp))
            LearnPulseProgressBar(progress = progress.overallProgress)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${(progress.overallProgress * 100).toInt()}% • ${progress.completedLessons.size} lessons done",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
