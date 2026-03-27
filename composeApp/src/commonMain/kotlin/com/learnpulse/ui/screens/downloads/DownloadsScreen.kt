package com.learnpulse.ui.screens.downloads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.domain.model.DownloadStatus
import com.learnpulse.domain.model.DownloadedLesson
import com.learnpulse.presentation.downloads.DownloadsEffect
import com.learnpulse.presentation.downloads.DownloadsIntent
import com.learnpulse.presentation.downloads.DownloadsViewModel
import com.learnpulse.ui.components.ErrorState
import com.learnpulse.ui.components.LoadingIndicator

class DownloadsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<DownloadsViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is DownloadsEffect.PlayLesson -> {}
                    is DownloadsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Downloads") },
                    actions = {
                        Row(modifier = Modifier.padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.WifiOff, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Offline Ready", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        ) { paddingValues ->
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(message = state.error!!, onRetry = { viewModel.onIntent(DownloadsIntent.LoadDownloads) })
                else -> Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    // Storage info
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Storage Used", style = MaterialTheme.typography.titleSmall)
                                Text(formatBytes(state.totalStorageBytes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.Download, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    if (state.downloads.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Download, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(16.dp))
                                Text("No downloads yet", style = MaterialTheme.typography.titleMedium)
                                Text("Download lessons to watch offline", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.downloads) { download ->
                                DownloadItem(
                                    download = download,
                                    onPlay = { viewModel.onIntent(DownloadsIntent.PlayDownload(download.lessonId)) },
                                    onDelete = { viewModel.onIntent(DownloadsIntent.DeleteDownload(download.lessonId)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadItem(download: DownloadedLesson, onPlay: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(download.title, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(formatBytes(download.fileSizeBytes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    StatusBadge(download.status)
                }
                if (download.status == DownloadStatus.IN_PROGRESS) {
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
            if (download.status == DownloadStatus.COMPLETED) {
                IconButton(onClick = onPlay) { Icon(Icons.Default.PlayArrow, null) }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        }
    }
}

@Composable
private fun StatusBadge(status: DownloadStatus) {
    val (text, color) = when (status) {
        DownloadStatus.COMPLETED -> "Downloaded" to androidx.compose.ui.graphics.Color(0xFF4CAF50)
        DownloadStatus.IN_PROGRESS -> "Downloading..." to MaterialTheme.colorScheme.primary
        DownloadStatus.QUEUED -> "Queued" to MaterialTheme.colorScheme.onSurfaceVariant
        DownloadStatus.FAILED -> "Failed" to MaterialTheme.colorScheme.error
        DownloadStatus.PAUSED -> "Paused" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(text, style = MaterialTheme.typography.labelSmall, color = color)
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_000_000_000L -> "${bytes / 1_000_000_000L} GB"
    bytes >= 1_000_000L -> "${bytes / 1_000_000L} MB"
    bytes >= 1_000L -> "${bytes / 1_000L} KB"
    else -> "$bytes B"
}
