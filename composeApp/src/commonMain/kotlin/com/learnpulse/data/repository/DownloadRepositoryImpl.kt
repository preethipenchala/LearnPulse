package com.learnpulse.data.repository

import com.learnpulse.data.local.LocalDataSource
import com.learnpulse.domain.model.DownloadStatus
import com.learnpulse.domain.model.DownloadedLesson
import com.learnpulse.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class DownloadRepositoryImpl(
    private val localDataSource: LocalDataSource
) : DownloadRepository {

    private val downloadProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val downloadStatuses = MutableStateFlow<Map<String, DownloadStatus>>(emptyMap())

    override fun getDownloadedLessons(): Flow<List<DownloadedLesson>> =
        localDataSource.getDownloadedLessons()

    override suspend fun downloadLesson(lessonId: String, courseId: String, title: String, url: String): Result<Unit> {
        return try {
            val download = DownloadedLesson(
                lessonId = lessonId,
                courseId = courseId,
                title = title,
                localFilePath = "",
                fileSizeBytes = 0L,
                downloadedAt = Clock.System.now().toEpochMilliseconds(),
                status = DownloadStatus.QUEUED
            )
            localDataSource.saveDownload(download)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDownloadProgress(lessonId: String): Flow<Float> =
        downloadProgress.map { it[lessonId] ?: 0f }

    override suspend fun cancelDownload(lessonId: String): Result<Unit> {
        return try {
            localDataSource.updateDownloadStatus(lessonId, DownloadStatus.FAILED)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDownload(lessonId: String): Result<Unit> {
        return try {
            localDataSource.deleteDownload(lessonId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTotalStorageUsedBytes(): Long =
        localDataSource.getTotalStorageUsedBytes()

    override fun getDownloadStatus(lessonId: String): Flow<DownloadStatus?> =
        downloadStatuses.map { it[lessonId] }
}
