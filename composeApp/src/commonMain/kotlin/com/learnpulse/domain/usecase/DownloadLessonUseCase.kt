package com.learnpulse.domain.usecase

import com.learnpulse.domain.repository.DownloadRepository

class DownloadLessonUseCase(private val downloadRepository: DownloadRepository) {
    suspend operator fun invoke(
        lessonId: String,
        courseId: String,
        title: String,
        url: String
    ): Result<Unit> = downloadRepository.downloadLesson(lessonId, courseId, title, url)
}
