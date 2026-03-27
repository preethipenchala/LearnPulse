package com.learnpulse.data.repository

import com.learnpulse.data.local.LocalDataSource
import com.learnpulse.domain.model.Bookmark
import com.learnpulse.domain.model.Note
import com.learnpulse.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class NotesRepositoryImpl(
    private val localDataSource: LocalDataSource
) : NotesRepository {

    override fun getNotes(userId: String, lessonId: String?): Flow<List<Note>> =
        localDataSource.getNotes(userId, lessonId)

    override fun searchNotes(userId: String, query: String): Flow<List<Note>> =
        localDataSource.searchNotes(userId, query)

    override suspend fun saveNote(note: Note): Result<Unit> {
        return try {
            localDataSource.saveNote(note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            localDataSource.deleteNote(noteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getBookmarks(userId: String): Flow<List<Bookmark>> =
        localDataSource.getBookmarks(userId)

    override suspend fun addBookmark(bookmark: Bookmark): Result<Unit> {
        return try {
            localDataSource.saveBookmark(bookmark)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeBookmark(bookmarkId: String): Result<Unit> {
        return try {
            localDataSource.deleteBookmark(bookmarkId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
