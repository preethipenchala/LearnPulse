package com.learnpulse.ui.screens.player

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.domain.model.Lesson
import com.learnpulse.presentation.player.PlayerEffect
import com.learnpulse.presentation.player.PlayerIntent
import com.learnpulse.presentation.player.PlayerViewModel
import com.learnpulse.ui.components.VideoPlayer
import com.learnpulse.ui.components.formatDuration
import com.learnpulse.ui.theme.LearnPulseTheme

data class PlayerScreen(val lesson: Lesson, val courseId: String) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<PlayerViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(lesson) {
            viewModel.onIntent(PlayerIntent.LoadLesson(lesson, courseId))
            viewModel.effect.collect { effect ->
                when (effect) {
                    is PlayerEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                    is PlayerEffect.NavigateBack -> navigator.pop()
                }
            }
        }

        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                // Video player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                ) {
                    VideoPlayer(
                        url = lesson.contentUrl,
                        isPlaying = state.isPlaying,
                        playbackSpeed = state.playbackSpeed,
                        onPositionChanged = { viewModel.onIntent(PlayerIntent.SeekTo(it)) },
                        onPlayPauseChanged = { viewModel.onIntent(PlayerIntent.PlayPause(it)) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Controls row
                PlayerControlsRow(
                    isPlaying = state.isPlaying,
                    playbackSpeed = state.playbackSpeed,
                    isCompleted = state.isCompleted,
                    showNotes = state.showNotes,
                    onPlayPause = { viewModel.onIntent(PlayerIntent.PlayPause(!state.isPlaying)) },
                    onSpeedChange = { viewModel.onIntent(PlayerIntent.ChangeSpeed(it)) },
                    onToggleNotes = { viewModel.onIntent(PlayerIntent.ToggleNotesPanel) },
                    onMarkComplete = { viewModel.onIntent(PlayerIntent.MarkComplete) }
                )

                // Notes panel
                AnimatedVisibility(visible = state.showNotes) {
                    NotesPanel(
                        notes = state.notes,
                        noteInputText = state.noteInputText,
                        onNoteTextChange = { viewModel.onIntent(PlayerIntent.NoteTextChanged(it)) },
                        onSaveNote = { viewModel.onIntent(PlayerIntent.SaveNote) },
                        onDeleteNote = { viewModel.onIntent(PlayerIntent.DeleteNote(it)) },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                // Lesson info
                if (!state.showNotes) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(lesson.title, style = MaterialTheme.typography.titleLarge)
                        Text(formatDuration(lesson.duration), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (lesson.description.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(lesson.description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerControlsRow(
    isPlaying: Boolean,
    playbackSpeed: Float,
    isCompleted: Boolean,
    showNotes: Boolean,
    onPlayPause: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onToggleNotes: () -> Unit,
    onMarkComplete: () -> Unit
) {
    var showSpeedMenu by remember { mutableStateOf(false) }
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPlayPause) {
            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
        }
        Box {
            TextButton(onClick = { showSpeedMenu = true }) {
                Text("${playbackSpeed}x")
            }
            DropdownMenu(expanded = showSpeedMenu, onDismissRequest = { showSpeedMenu = false }) {
                speeds.forEach { speed ->
                    DropdownMenuItem(
                        text = { Text("${speed}x") },
                        onClick = { onSpeedChange(speed); showSpeedMenu = false }
                    )
                }
            }
        }
        IconButton(onClick = onToggleNotes) {
            Icon(Icons.Default.Notes, null, tint = if (showNotes) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        }
        Button(
            onClick = onMarkComplete,
            enabled = !isCompleted,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Check, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(if (isCompleted) "Completed" else "Mark Complete")
        }
    }
}

@Composable
private fun NotesPanel(
    notes: List<com.learnpulse.domain.model.Note>,
    noteInputText: String,
    onNoteTextChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onDeleteNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = noteInputText,
                onValueChange = onNoteTextChange,
                placeholder = { Text("Add a note...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onSaveNote, enabled = noteInputText.isNotBlank()) {
                Icon(Icons.Default.Send, null)
            }
        }
        LazyColumn {
            items(notes) { note ->
                NoteItem(note = note, onDelete = { onDeleteNote(note.id) })
            }
        }
    }
}

@Composable
private fun NoteItem(note: com.learnpulse.domain.model.Note, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(note.content, style = MaterialTheme.typography.bodyMedium) },
        supportingContent = {
            if (note.timestampSeconds > 0) {
                Text("At ${formatDuration(note.timestampSeconds)}", style = MaterialTheme.typography.bodySmall)
            }
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
            }
        }
    )
    HorizontalDivider()
}
