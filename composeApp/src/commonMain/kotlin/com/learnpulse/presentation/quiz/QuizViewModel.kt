package com.learnpulse.presentation.quiz

import com.learnpulse.domain.model.Quiz
import com.learnpulse.domain.model.QuizQuestion
import com.learnpulse.domain.repository.ProgressRepository
import com.learnpulse.domain.repository.QuizRepository
import com.learnpulse.domain.repository.UserRepository
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class QuizState(
    val isLoading: Boolean = true,
    val quiz: Quiz? = null,
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val answeredQuestions: Map<Int, Int> = emptyMap(),
    val showExplanation: Boolean = false,
    val isFinished: Boolean = false,
    val score: Int = 0,
    val remainingTimeSeconds: Long? = null,
    val error: String? = null
) : UiState {
    val currentQuestion: QuizQuestion? get() = quiz?.questions?.getOrNull(currentQuestionIndex)
    val totalQuestions: Int get() = quiz?.questions?.size ?: 0
    val isLastQuestion: Boolean get() = currentQuestionIndex == totalQuestions - 1
    val isCorrect: Boolean? get() {
        val selected = selectedOptionIndex ?: return null
        return selected == currentQuestion?.correctIndex
    }
}

sealed interface QuizIntent : UiIntent {
    data class LoadQuiz(val lessonId: String, val courseId: String) : QuizIntent
    data class OptionSelected(val optionIndex: Int) : QuizIntent
    data object SubmitAnswer : QuizIntent
    data object NextQuestion : QuizIntent
    data object FinishQuiz : QuizIntent
    data object RetryQuiz : QuizIntent
}

sealed interface QuizEffect : UiEffect {
    data object NavigateBack : QuizEffect
    data class ShowMessage(val message: String) : QuizEffect
}

class QuizViewModel(
    private val quizRepository: QuizRepository,
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository
) : BaseViewModel<QuizState, QuizIntent, QuizEffect>(QuizState()) {

    private var courseId: String = ""

    override fun onIntent(intent: QuizIntent) {
        when (intent) {
            is QuizIntent.LoadQuiz -> {
                courseId = intent.courseId
                loadQuiz(intent.lessonId)
            }
            is QuizIntent.OptionSelected -> {
                if (!state.value.showExplanation) {
                    updateState { copy(selectedOptionIndex = intent.optionIndex) }
                }
            }
            is QuizIntent.SubmitAnswer -> submitAnswer()
            is QuizIntent.NextQuestion -> nextQuestion()
            is QuizIntent.FinishQuiz -> finishQuiz()
            is QuizIntent.RetryQuiz -> {
                val quiz = state.value.quiz
                updateState {
                    QuizState(isLoading = false, quiz = quiz, remainingTimeSeconds = quiz?.timeLimit)
                }
            }
        }
    }

    private fun loadQuiz(lessonId: String) {
        screenModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            try {
                val quiz = quizRepository.getQuizForLesson(lessonId)
                updateState {
                    copy(
                        isLoading = false,
                        quiz = quiz,
                        remainingTimeSeconds = quiz?.timeLimit
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun submitAnswer() {
        val selected = state.value.selectedOptionIndex ?: return
        updateState {
            copy(
                showExplanation = true,
                answeredQuestions = answeredQuestions + (currentQuestionIndex to selected)
            )
        }
    }

    private fun nextQuestion() {
        updateState {
            copy(
                currentQuestionIndex = currentQuestionIndex + 1,
                selectedOptionIndex = null,
                showExplanation = false
            )
        }
    }

    private fun finishQuiz() {
        screenModelScope.launch {
            val st = state.value
            val quiz = st.quiz ?: return@launch
            val correctCount = st.answeredQuestions.entries.count { (idx, answer) ->
                answer == quiz.questions[idx].correctIndex
            }
            val score = (correctCount * 100) / quiz.questions.size
            updateState { copy(isFinished = true, score = score) }

            val user = userRepository.getCurrentUser().first()
            if (user != null) {
                progressRepository.saveQuizScore(user.id, courseId, quiz.lessonId, score)
            }
        }
    }
}
