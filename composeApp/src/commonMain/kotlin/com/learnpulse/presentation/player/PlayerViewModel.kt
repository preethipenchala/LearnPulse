package com.learnpulse.presentation.player

import com.learnpulse.domain.model.Lesson
import com.learnpulse.domain.model.Note
import com.learnpulse.domain.repository.NotesRepository
import com.learnpulse.domain.repository.ProgressRepository
import com.learnpulse.domain.repository.UserRepository
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class PlayerState(
    val lesson: Lesson? = null,
    val isPlaying: Boolean = false,
    val currentPositionSeconds: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val isFullscreen: Boolean = false,
    val showNotes: Boolean = false,
    val notes: List<Note> = emptyList(),
    val noteInputText: String = "",
    val isCompleted: Boolean = false,
    val error: String? = null
) : UiState

sealed interface PlayerIntent : UiIntent {
    data class LoadLesson(val lesson: Lesson, val courseId: String) : PlayerIntent
    data class PlayPause(val isPlaying: Boolean) : PlayerIntent
    data class SeekTo(val positionSeconds: Long) : PlayerIntent
    data class ChangeSpeed(val speed: Float) : PlayerIntent
    data object ToggleFullscreen : PlayerIntent
    data object ToggleNotesPanel : PlayerIntent
    data class NoteTextChanged(val text: String) : PlayerIntent
    data object SaveNote : PlayerIntent
    data object MarkComplete : PlayerIntent
    data class DeleteNote(val noteId: String) : PlayerIntent
}

sealed interface PlayerEffect : UiEffect {
    data class ShowMessage(val message: String) : PlayerEffect
    data object NavigateBack : PlayerEffect
}

class PlayerViewModel(
    private val progressRepository: ProgressRepository,
    private val notesRepository: NotesRepository,
    private val userRepository: UserRepository
) : BaseViewModel<PlayerState, PlayerIntent, PlayerEffect>(PlayerState()) {

    private var currentCourseId: String = ""

    override fun onIntent(intent: PlayerIntent) {
        when (intent) {
            is PlayerIntent.LoadLesson -> {
                currentCourseId = intent.courseId
                loadLesson(intent.lesson)
            }
            is PlayerIntent.PlayPause -> updateState { copy(isPlaying = intent.isPlaying) }
            is PlayerIntent.SeekTo -> updateState { copy(currentPositionSeconds = intent.positionSeconds) }
            is PlayerIntent.ChangeSpeed -> updateState { copy(playbackSpeed = intent.speed) }
            is PlayerIntent.ToggleFullscreen -> updateState { copy(isFullscreen = !isFullscreen) }
            is PlayerIntent.ToggleNotesPanel -> updateState { copy(showNotes = !showNotes) }
            is PlayerIntent.NoteTextChanged -> updateState { copy(noteInputText = intent.text) }
            is PlayerIntent.SaveNote -> saveNote()
            is PlayerIntent.MarkComplete -> markComplete()
            is PlayerIntent.DeleteNote -> deleteNote(intent.noteId)
        }
    }

    private fun loadLesson(lesson: Lesson) {
        screenModelScope.launch {
            updateState { copy(lesson = lesson) }
            val user = userRepository.getCurrentUser().first()
            if (user != null) {
                progressRepository.updateLastAccessed(user.id, currentCourseId, lesson.id)
                val notes = notesRepository.getNotes(user.id, lesson.id).first()
                updateState { copy(notes = notes) }
            }
        }
    }

    private fun saveNote() {
        screenModelScope.launch {
            val text = state.value.noteInputText.trim()
            if (text.isBlank()) return@launch
            val user = userRepository.getCurrentUser().first() ?: return@launch
            val lesson = state.value.lesson ?: return@launch
            val note = Note(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                userId = user.id,
                courseId = currentCourseId,
                lessonId = lesson.id,
                content = text,
                timestampSeconds = state.value.currentPositionSeconds,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds()
            )
            notesRepository.saveNote(note)
                .onSuccess {
                    val updatedNotes = notesRepository.getNotes(user.id, lesson.id).first()
                    updateState { copy(notes = updatedNotes, noteInputText = "") }
                    emitEffect(PlayerEffect.ShowMessage("Note saved"))
                }
                .onFailure {
                    emitEffect(PlayerEffect.ShowMessage("Failed to save note"))
                }
        }
    }

    private fun markComplete() {
        screenModelScope.launch {
            val user = userRepository.getCurrentUser().first() ?: return@launch
            val lesson = state.value.lesson ?: return@launch
            progressRepository.markLessonComplete(user.id, currentCourseId, lesson.id)
            updateState { copy(isCompleted = true) }
            emitEffect(PlayerEffect.ShowMessage("Lesson completed!"))
        }
    }

    private fun deleteNote(noteId: String) {
        screenModelScope.launch {
            notesRepository.deleteNote(noteId)
            updateState { copy(notes = notes.filter { it.id != noteId }) }
        }
    }
}
