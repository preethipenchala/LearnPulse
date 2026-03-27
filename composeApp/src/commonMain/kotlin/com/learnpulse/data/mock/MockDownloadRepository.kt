package com.learnpulse.data.mock

import com.learnpulse.domain.model.DownloadStatus
import com.learnpulse.domain.model.DownloadedLesson
import com.learnpulse.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MockDownloadRepository : DownloadRepository {

    private val _downloads = MutableStateFlow(MockDataSource.downloads.toList())
    private val _progress = MutableStateFlow<Map<String, Float>>(
        mapOf("course-2-l1" to 0.62f) // course-2-l1 is IN_PROGRESS at 62%
    )

    override fun getDownloadedLessons(): Flow<List<DownloadedLesson>> = _downloads

    override suspend fun downloadLesson(
        lessonId: String,
        courseId: String,
        title: String,
        url: String
    ): Result<Unit> {
        val existing = _downloads.value.find { it.lessonId == lessonId }
        if (existing == null) {
            val newDownload = DownloadedLesson(
                lessonId = lessonId,
                courseId = courseId,
                title = title,
                localFilePath = "/data/user/0/com.learnpulse.android/files/downloads/$lessonId.mp4",
                fileSizeBytes = 0,
                downloadedAt = 0L,
                status = DownloadStatus.QUEUED
            )
            _downloads.value = _downloads.value + newDownload
            _progress.value = _progress.value + (lessonId to 0f)
        }
        return Result.success(Unit)
    }

    override fun getDownloadProgress(lessonId: String): Flow<Float> =
        _progress.map { it[lessonId] ?: 0f }

    override suspend fun cancelDownload(lessonId: String): Result<Unit> {
        _downloads.value = _downloads.value.map { dl ->
            if (dl.lessonId == lessonId) dl.copy(status = DownloadStatus.PAUSED) else dl
        }
        return Result.success(Unit)
    }

    override suspend fun deleteDownload(lessonId: String): Result<Unit> {
        _downloads.value = _downloads.value.filter { it.lessonId != lessonId }
        _progress.value = _progress.value - lessonId
        return Result.success(Unit)
    }

    override suspend fun getTotalStorageUsedBytes(): Long =
        _downloads.value
            .filter { it.status == DownloadStatus.COMPLETED }
            .sumOf { it.fileSizeBytes }

    override fun getDownloadStatus(lessonId: String): Flow<DownloadStatus?> =
        _downloads.map { list -> list.find { it.lessonId == lessonId }?.status }
}
