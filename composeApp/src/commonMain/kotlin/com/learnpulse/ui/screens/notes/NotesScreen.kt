package com.learnpulse.ui.screens.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learnpulse.presentation.notes.NotesEffect
import com.learnpulse.presentation.notes.NotesIntent
import com.learnpulse.presentation.notes.NotesViewModel
import com.learnpulse.ui.components.ErrorState
import com.learnpulse.ui.components.LoadingIndicator

class NotesScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<NotesViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is NotesEffect.NavigateToLesson -> {}
                    is NotesEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { TopAppBar(title = { Text("Notes & Bookmarks") }) }
        ) { paddingValues ->
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorState(message = state.error!!, onRetry = { viewModel.onIntent(NotesIntent.LoadNotes) })
                else -> Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onIntent(NotesIntent.SearchQueryChanged(it)) },
                        placeholder = { Text("Search notes...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    TabRow(selectedTabIndex = state.selectedTab) {
                        Tab(
                            selected = state.selectedTab == 0,
                            onClick = { viewModel.onIntent(NotesIntent.TabSelected(0)) },
                            text = { Text("Notes") },
                            icon = { Icon(Icons.Default.Notes, null) }
                        )
                        Tab(
                            selected = state.selectedTab == 1,
                            onClick = { viewModel.onIntent(NotesIntent.TabSelected(1)) },
                            text = { Text("Bookmarks") },
                            icon = { Icon(Icons.Default.Bookmark, null) }
                        )
                    }
                    when (state.selectedTab) {
                        0 -> NotesList(
                            notes = state.notes,
                            onDelete = { viewModel.onIntent(NotesIntent.DeleteNote(it)) }
                        )
                        1 -> BookmarksList(
                            bookmarks = state.bookmarks,
                            onRemove = { viewModel.onIntent(NotesIntent.RemoveBookmark(it)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesList(
    notes: List<com.learnpulse.domain.model.Note>,
    onDelete: (String) -> Unit
) {
    if (notes.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No notes yet. Add notes while watching lessons.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(notes) { note ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Lesson: ${note.lessonId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onDelete(note.id) }) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BookmarksList(
    bookmarks: List<com.learnpulse.domain.model.Bookmark>,
    onRemove: (String) -> Unit
) {
    if (bookmarks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No bookmarks yet.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(bookmarks) { bookmark ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Default.Bookmark, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(bookmark.title, style = MaterialTheme.typography.bodyMedium)
                    }
                    IconButton(onClick = { onRemove(bookmark.id) }) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
