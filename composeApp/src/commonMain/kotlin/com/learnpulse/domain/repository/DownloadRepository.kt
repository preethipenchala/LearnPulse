package com.learnpulse.domain.repository

import com.learnpulse.domain.model.DownloadStatus
import com.learnpulse.domain.model.DownloadedLesson
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getDownloadedLessons(): Flow<List<DownloadedLesson>>

    suspend fun downloadLesson(lessonId: String, courseId: String, title: String, url: String): Result<Unit>

    fun getDownloadProgress(lessonId: String): Flow<Float>

    suspend fun cancelDownload(lessonId: String): Result<Unit>

    suspend fun deleteDownload(lessonId: String): Result<Unit>

    suspend fun getTotalStorageUsedBytes(): Long

    fun getDownloadStatus(lessonId: String): Flow<DownloadStatus?>
}
