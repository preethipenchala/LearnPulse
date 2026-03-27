package com.learnpulse.domain.usecase

import com.learnpulse.domain.model.Note
import com.learnpulse.domain.model.Bookmark
import com.learnpulse.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val notesRepository: NotesRepository) {
    fun getNotes(userId: String, lessonId: String? = null): Flow<List<Note>> =
        notesRepository.getNotes(userId, lessonId)

    fun searchNotes(userId: String, query: String): Flow<List<Note>> =
        notesRepository.searchNotes(userId, query)

    fun getBookmarks(userId: String): Flow<List<Bookmark>> =
        notesRepository.getBookmarks(userId)
}
