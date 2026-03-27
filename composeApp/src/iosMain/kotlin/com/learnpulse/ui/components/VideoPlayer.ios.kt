package com.learnpulse.ui.components

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import platform.AVFoundation.*
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    isPlaying: Boolean,
    playbackSpeed: Float,
    onPositionChanged: (Long) -> Unit,
    onPlayPauseChanged: (Boolean) -> Unit,
    modifier: Modifier
) {
    // Re-create player only when the URL changes
    val player: AVPlayer? = remember(url) {
        NSURL.URLWithString(url)?.let { AVPlayer(uRL = it) }
    }

    // Single AVPlayerViewController instance, updated via `update`
    val playerViewController = remember { AVPlayerViewController() }

    // Play / pause
    LaunchedEffect(isPlaying, player) {
        if (player == null) return@LaunchedEffect
        if (isPlaying) {
            player.play()
        } else {
            player.pause()
        }
        onPlayPauseChanged(isPlaying)
    }

    // Playback speed
    LaunchedEffect(playbackSpeed, player) {
        player?.rate = if (isPlaying) playbackSpeed else 0f
    }

    // Report position every second while playing
    LaunchedEffect(isPlaying, player) {
        while (isPlaying && player != null) {
            val seconds = CMTimeGetSeconds(player.currentTime()).toLong()
            onPositionChanged(seconds)
            delay(1_000L)
        }
    }

    DisposableEffect(player) {
        onDispose {
            player?.pause()
        }
    }

    UIKitView(
        factory = {
            playerViewController.showsPlaybackControls = true
            playerViewController.view
        },
        update = { _ ->
            // Attach or swap player when url/player reference changes
            if (playerViewController.player !== player) {
                playerViewController.player = player
            }
        },
        modifier = modifier
    )
}
