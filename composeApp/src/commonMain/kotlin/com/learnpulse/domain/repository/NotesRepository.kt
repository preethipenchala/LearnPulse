package com.learnpulse.domain.repository

import com.learnpulse.domain.model.Bookmark
import com.learnpulse.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getNotes(userId: String, lessonId: String? = null): Flow<List<Note>>

    fun searchNotes(userId: String, query: String): Flow<List<Note>>

    suspend fun saveNote(note: Note): Result<Unit>

    suspend fun deleteNote(noteId: String): Result<Unit>

    fun getBookmarks(userId: String): Flow<List<Bookmark>>

    suspend fun addBookmark(bookmark: Bookmark): Result<Unit>

    suspend fun removeBookmark(bookmarkId: String): Result<Unit>
}
