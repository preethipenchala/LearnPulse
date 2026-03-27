package com.learnpulse.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun VideoPlayer(
    url: String,
    isPlaying: Boolean,
    playbackSpeed: Float,
    onPositionChanged: (Long) -> Unit,
    onPlayPauseChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
)
