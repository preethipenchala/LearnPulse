package com.learnpulse.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.presentation.quiz.QuizEffect
import com.learnpulse.presentation.quiz.QuizIntent
import com.learnpulse.presentation.quiz.QuizViewModel
import com.learnpulse.ui.components.ErrorState
import com.learnpulse.ui.components.LoadingIndicator

data class QuizScreen(val lessonId: String, val courseId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<QuizViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(QuizIntent.LoadQuiz(lessonId, courseId))
            viewModel.effect.collect { effect ->
                when (effect) {
                    is QuizEffect.NavigateBack -> navigator.pop()
                    is QuizEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Quiz") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(message = state.error!!, onRetry = { viewModel.onIntent(QuizIntent.LoadQuiz(lessonId, courseId)) })
                state.isFinished -> QuizResults(
                    score = state.score,
                    passingScore = state.quiz?.passingScore ?: 70,
                    totalQuestions = state.totalQuestions,
                    correctCount = state.answeredQuestions.entries.count { (idx, answer) ->
                        answer == state.quiz?.questions?.getOrNull(idx)?.correctIndex
                    },
                    onRetry = { viewModel.onIntent(QuizIntent.RetryQuiz) },
                    onExit = { navigator.pop() },
                    modifier = Modifier.padding(paddingValues)
                )
                state.quiz != null -> QuizContent(
                    state = state,
                    onIntent = viewModel::onIntent,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun QuizContent(
    state: com.learnpulse.presentation.quiz.QuizState,
    onIntent: (QuizIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = state.currentQuestion ?: return
    val progressAnim by animateFloatAsState(
        targetValue = (state.currentQuestionIndex + 1f) / state.totalQuestions,
        animationSpec = tween(500),
        label = "quiz_progress"
    )

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Progress
        Text(
            text = "Question ${state.currentQuestionIndex + 1} of ${state.totalQuestions}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progressAnim },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))

        // Question
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(20.dp)
            )
        }
        Spacer(Modifier.height(20.dp))

        // Options
        question.options.forEachIndexed { index, option ->
            val isSelected = state.selectedOptionIndex == index
            val isCorrect = index == question.correctIndex
            val backgroundColor = when {
                state.showExplanation && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                state.showExplanation && isSelected && !isCorrect -> Color(0xFFEF5350).copy(alpha = 0.15f)
                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else -> MaterialTheme.colorScheme.surface
            }
            val borderColor = when {
                state.showExplanation && isCorrect -> Color(0xFF4CAF50)
                state.showExplanation && isSelected && !isCorrect -> Color(0xFFEF5350)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }
            OutlinedCard(
                onClick = { if (!state.showExplanation) onIntent(QuizIntent.OptionSelected(index)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, borderColor),
                colors = CardDefaults.outlinedCardColors(containerColor = backgroundColor)
            ) {
                Text(option, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
            }
        }
        Spacer(Modifier.height(16.dp))

        // Explanation
        AnimatedVisibility(visible = state.showExplanation) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.isCorrect == true)
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else Color(0xFFEF5350).copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (state.isCorrect == true) "✅ Correct!" else "❌ Incorrect",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (state.isCorrect == true) Color(0xFF4CAF50) else Color(0xFFEF5350)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(question.explanation, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.weight(1f))
        // Action button
        Button(
            onClick = {
                when {
                    !state.showExplanation && state.selectedOptionIndex != null -> onIntent(QuizIntent.SubmitAnswer)
                    state.showExplanation && !state.isLastQuestion -> onIntent(QuizIntent.NextQuestion)
                    state.showExplanation && state.isLastQuestion -> onIntent(QuizIntent.FinishQuiz)
                }
            },
            enabled = state.selectedOptionIndex != null || state.showExplanation,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    !state.showExplanation -> "Submit Answer"
                    state.isLastQuestion -> "See Results"
                    else -> "Next Question"
                }
            )
        }
    }
}

@Composable
private fun QuizResults(
    score: Int,
    passingScore: Int,
    totalQuestions: Int,
    correctCount: Int,
    onRetry: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val passed = score >= passingScore
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (passed) "🎉" else "😕", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (passed) "Congratulations!" else "Keep Learning!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "You scored $score%",
            style = MaterialTheme.typography.headlineSmall,
            color = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "$correctCount out of $totalQuestions correct",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Passing score: $passingScore%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onRetry) { Text("Retry Quiz") }
            Button(onClick = onExit) { Text("Continue") }
        }
    }
}
