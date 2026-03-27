package com.learnpulse.presentation.notes

import com.learnpulse.domain.model.Bookmark
import com.learnpulse.domain.model.Note
import com.learnpulse.domain.repository.NotesRepository
import com.learnpulse.domain.repository.UserRepository
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class NotesState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val bookmarks: List<Bookmark> = emptyList(),
    val searchQuery: String = "",
    val selectedTab: Int = 0,
    val error: String? = null
) : UiState

sealed interface NotesIntent : UiIntent {
    data object LoadNotes : NotesIntent
    data class SearchQueryChanged(val query: String) : NotesIntent
    data class TabSelected(val tab: Int) : NotesIntent
    data class DeleteNote(val noteId: String) : NotesIntent
    data class RemoveBookmark(val bookmarkId: String) : NotesIntent
    data class NavigateToLesson(val lessonId: String, val courseId: String) : NotesIntent
}

sealed interface NotesEffect : UiEffect {
    data class NavigateToLesson(val lessonId: String, val courseId: String) : NotesEffect
    data class ShowMessage(val message: String) : NotesEffect
}

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val userRepository: UserRepository
) : BaseViewModel<NotesState, NotesIntent, NotesEffect>(NotesState()) {

    init {
        onIntent(NotesIntent.LoadNotes)
    }

    override fun onIntent(intent: NotesIntent) {
        when (intent) {
            is NotesIntent.LoadNotes -> loadNotes()
            is NotesIntent.SearchQueryChanged -> {
                updateState { copy(searchQuery = intent.query) }
                searchNotes(intent.query)
            }
            is NotesIntent.TabSelected -> updateState { copy(selectedTab = intent.tab) }
            is NotesIntent.DeleteNote -> deleteNote(intent.noteId)
            is NotesIntent.RemoveBookmark -> removeBookmark(intent.bookmarkId)
            is NotesIntent.NavigateToLesson -> emitEffect(NotesEffect.NavigateToLesson(intent.lessonId, intent.courseId))
        }
    }

    private fun loadNotes() {
        screenModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            try {
                val user = userRepository.getCurrentUser().first() ?: return@launch
                val notes = notesRepository.getNotes(user.id).first()
                val bookmarks = notesRepository.getBookmarks(user.id).first()
                updateState { copy(isLoading = false, notes = notes, bookmarks = bookmarks) }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun searchNotes(query: String) {
        screenModelScope.launch {
            val user = userRepository.getCurrentUser().first() ?: return@launch
            val notes = if (query.isBlank()) {
                notesRepository.getNotes(user.id).first()
            } else {
                notesRepository.searchNotes(user.id, query).first()
            }
            updateState { copy(notes = notes) }
        }
    }

    private fun deleteNote(noteId: String) {
        screenModelScope.launch {
            notesRepository.deleteNote(noteId)
            updateState { copy(notes = notes.filter { it.id != noteId }) }
            emitEffect(NotesEffect.ShowMessage("Note deleted"))
        }
    }

    private fun removeBookmark(bookmarkId: String) {
        screenModelScope.launch {
            notesRepository.removeBookmark(bookmarkId)
            updateState { copy(bookmarks = bookmarks.filter { it.id != bookmarkId }) }
        }
    }
}
