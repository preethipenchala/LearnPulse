package com.learnpulse.data.mock

import com.learnpulse.domain.model.Bookmark
import com.learnpulse.domain.model.Note
import com.learnpulse.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MockNotesRepository : NotesRepository {

    private val _notes = MutableStateFlow(MockDataSource.notes.toList())
    private val _bookmarks = MutableStateFlow(MockDataSource.bookmarks.toList())

    override fun getNotes(userId: String, lessonId: String?): Flow<List<Note>> =
        _notes.map { notes ->
            notes.filter { it.userId == userId && (lessonId == null || it.lessonId == lessonId) }
                .sortedByDescending { it.createdAt }
        }

    override fun searchNotes(userId: String, query: String): Flow<List<Note>> =
        _notes.map { notes ->
            val q = query.lowercase()
            notes.filter { it.userId == userId && it.content.lowercase().contains(q) }
                .sortedByDescending { it.createdAt }
        }

    override suspend fun saveNote(note: Note): Result<Unit> {
        val current = _notes.value.toMutableList()
        val index = current.indexOfFirst { it.id == note.id }
        if (index >= 0) {
            current[index] = note
        } else {
            current.add(0, note)
        }
        _notes.value = current
        return Result.success(Unit)
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        _notes.value = _notes.value.filter { it.id != noteId }
        return Result.success(Unit)
    }

    override fun getBookmarks(userId: String): Flow<List<Bookmark>> =
        _bookmarks.map { bookmarks ->
            bookmarks.filter { it.userId == userId }.sortedByDescending { it.createdAt }
        }

    override suspend fun addBookmark(bookmark: Bookmark): Result<Unit> {
        if (_bookmarks.value.none { it.id == bookmark.id }) {
            _bookmarks.value = listOf(bookmark) + _bookmarks.value
        }
        return Result.success(Unit)
    }

    override suspend fun removeBookmark(bookmarkId: String): Result<Unit> {
        _bookmarks.value = _bookmarks.value.filter { it.id != bookmarkId }
        return Result.success(Unit)
    }
}
