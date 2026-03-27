package com.learnpulse.presentation.aipractice

import com.learnpulse.domain.model.Difficulty
import com.learnpulse.domain.model.Quiz
import com.learnpulse.domain.usecase.GenerateQuizUseCase
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch

data class AiPracticeState(
    val topic: String = "",
    val selectedDifficulty: Difficulty = Difficulty.BEGINNER,
    val questionCount: Int = 10,
    val isGenerating: Boolean = false,
    val generatedQuiz: Quiz? = null,
    val error: String? = null
) : UiState

sealed interface AiPracticeIntent : UiIntent {
    data class TopicChanged(val topic: String) : AiPracticeIntent
    data class DifficultySelected(val difficulty: Difficulty) : AiPracticeIntent
    data class QuestionCountChanged(val count: Int) : AiPracticeIntent
    data object GenerateQuiz : AiPracticeIntent
    data class StartQuiz(val quiz: Quiz) : AiPracticeIntent
}

sealed interface AiPracticeEffect : UiEffect {
    data class NavigateToQuiz(val quiz: Quiz) : AiPracticeEffect
    data class ShowError(val message: String) : AiPracticeEffect
}

class AiPracticeViewModel(
    private val generateQuizUseCase: GenerateQuizUseCase
) : BaseViewModel<AiPracticeState, AiPracticeIntent, AiPracticeEffect>(AiPracticeState()) {

    override fun onIntent(intent: AiPracticeIntent) {
        when (intent) {
            is AiPracticeIntent.TopicChanged -> updateState { copy(topic = intent.topic) }
            is AiPracticeIntent.DifficultySelected -> updateState { copy(selectedDifficulty = intent.difficulty) }
            is AiPracticeIntent.QuestionCountChanged -> updateState { copy(questionCount = intent.count) }
            is AiPracticeIntent.GenerateQuiz -> generateQuiz()
            is AiPracticeIntent.StartQuiz -> emitEffect(AiPracticeEffect.NavigateToQuiz(intent.quiz))
        }
    }

    private fun generateQuiz() {
        screenModelScope.launch {
            val st = state.value
            if (st.topic.isBlank()) {
                emitEffect(AiPracticeEffect.ShowError("Please enter a topic"))
                return@launch
            }
            updateState { copy(isGenerating = true, error = null) }
            val result = generateQuizUseCase(st.topic, st.selectedDifficulty.name, st.questionCount)
            if (result.isSuccess) {
                updateState { copy(isGenerating = false, generatedQuiz = result.getOrNull()) }
            } else {
                updateState { copy(isGenerating = false, error = result.exceptionOrNull()?.message) }
                emitEffect(AiPracticeEffect.ShowError(result.exceptionOrNull()?.message ?: "Failed to generate quiz"))
            }
        }
    }
}
