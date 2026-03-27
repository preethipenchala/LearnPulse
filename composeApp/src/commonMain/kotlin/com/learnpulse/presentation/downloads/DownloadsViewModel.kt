package com.learnpulse.presentation.downloads

import com.learnpulse.domain.model.DownloadedLesson
import com.learnpulse.domain.repository.DownloadRepository
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class DownloadsState(
    val isLoading: Boolean = true,
    val downloads: List<DownloadedLesson> = emptyList(),
    val totalStorageBytes: Long = 0L,
    val error: String? = null
) : UiState

sealed interface DownloadsIntent : UiIntent {
    data object LoadDownloads : DownloadsIntent
    data class DeleteDownload(val lessonId: String) : DownloadsIntent
    data class PlayDownload(val lessonId: String) : DownloadsIntent
}

sealed interface DownloadsEffect : UiEffect {
    data class PlayLesson(val lessonId: String) : DownloadsEffect
    data class ShowMessage(val message: String) : DownloadsEffect
}

class DownloadsViewModel(
    private val downloadRepository: DownloadRepository
) : BaseViewModel<DownloadsState, DownloadsIntent, DownloadsEffect>(DownloadsState()) {

    init {
        onIntent(DownloadsIntent.LoadDownloads)
    }

    override fun onIntent(intent: DownloadsIntent) {
        when (intent) {
            is DownloadsIntent.LoadDownloads -> loadDownloads()
            is DownloadsIntent.DeleteDownload -> deleteDownload(intent.lessonId)
            is DownloadsIntent.PlayDownload -> emitEffect(DownloadsEffect.PlayLesson(intent.lessonId))
        }
    }

    private fun loadDownloads() {
        screenModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            downloadRepository.getDownloadedLessons().onEach { downloads ->
                val totalStorage = downloadRepository.getTotalStorageUsedBytes()
                updateState {
                    copy(
                        isLoading = false,
                        downloads = downloads,
                        totalStorageBytes = totalStorage
                    )
                }
            }.launchIn(screenModelScope)
        }
    }

    private fun deleteDownload(lessonId: String) {
        screenModelScope.launch {
            val result = downloadRepository.deleteDownload(lessonId)
            if (result.isSuccess) {
                emitEffect(DownloadsEffect.ShowMessage("Download deleted"))
            } else {
                emitEffect(DownloadsEffect.ShowMessage("Failed to delete download"))
            }
        }
    }
}
