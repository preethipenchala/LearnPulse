package com.learnpulse.ui.screens.aipractice

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.domain.model.Difficulty
import com.learnpulse.presentation.aipractice.AiPracticeEffect
import com.learnpulse.presentation.aipractice.AiPracticeIntent
import com.learnpulse.presentation.aipractice.AiPracticeViewModel
import com.learnpulse.ui.components.CategoryChip
import com.learnpulse.ui.screens.quiz.QuizScreen
import com.learnpulse.ui.theme.LearnPulseTheme

class AiPracticeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<AiPracticeViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AiPracticeEffect.NavigateToQuiz -> navigator.push(QuizScreen(effect.quiz.lessonId, ""))
                    is AiPracticeEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { TopAppBar(title = { Text("AI Practice") }) }
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                            Text("Generate Practice Quiz", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Enter a topic and our AI will generate a personalized quiz for you.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = state.topic,
                    onValueChange = { viewModel.onIntent(AiPracticeIntent.TopicChanged(it)) },
                    label = { Text("Topic (e.g. Python functions, React hooks)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Difficulty", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { diff ->
                        CategoryChip(
                            label = diff.displayName(),
                            selected = state.selectedDifficulty == diff,
                            onClick = { viewModel.onIntent(AiPracticeIntent.DifficultySelected(diff)) },
                            color = when (diff) {
                                Difficulty.BEGINNER -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                Difficulty.INTERMEDIATE -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                Difficulty.ADVANCED -> androidx.compose.ui.graphics.Color(0xFFEF5350)
                            }
                        )
                    }
                }

                Text("Number of Questions: ${state.questionCount}", style = MaterialTheme.typography.titleSmall)
                Slider(
                    value = state.questionCount.toFloat(),
                    onValueChange = { viewModel.onIntent(AiPracticeIntent.QuestionCountChanged(it.toInt())) },
                    valueRange = 5f..20f,
                    steps = 2
                )

                state.generatedQuiz?.let { quiz ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Quiz Ready!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("${quiz.questions.size} questions generated on ${state.topic}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.onIntent(AiPracticeIntent.StartQuiz(quiz)) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Start Quiz")
                            }
                        }
                    }
                }

                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { viewModel.onIntent(AiPracticeIntent.GenerateQuiz) },
                    enabled = !state.isGenerating && state.topic.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (state.isGenerating) "Generating..." else "Generate Quiz")
                }
            }
        }
    }
}
