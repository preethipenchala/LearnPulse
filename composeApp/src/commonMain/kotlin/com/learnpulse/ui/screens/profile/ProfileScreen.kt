package com.learnpulse.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.learnpulse.presentation.profile.ProfileEffect
import com.learnpulse.presentation.profile.ProfileIntent
import com.learnpulse.presentation.profile.ProfileViewModel
import com.learnpulse.ui.components.LoadingIndicator

class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<ProfileViewModel>()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ProfileEffect.NavigateToLogin -> navigator.popUntilRoot()
                    is ProfileEffect.ShareCertificate -> {}
                    is ProfileEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = { TopAppBar(title = { Text("Profile") }) }
        ) { paddingValues ->
            if (state.isLoading) {
                LoadingIndicator()
                return@Scaffold
            }
            val user = state.user
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // User info
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = user?.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(user?.name ?: "User", style = MaterialTheme.typography.titleLarge)
                            Text(user?.email ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${user?.enrolledCourseIds?.size ?: 0}", style = MaterialTheme.typography.titleMedium)
                                    Text("Enrolled", style = MaterialTheme.typography.bodySmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${user?.completedCourseIds?.size ?: 0}", style = MaterialTheme.typography.titleMedium)
                                    Text("Completed", style = MaterialTheme.typography.bodySmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${user?.streakDays ?: 0}🔥", style = MaterialTheme.typography.titleMedium)
                                    Text("Streak", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                // Certificates
                if (user?.certificates?.isNotEmpty() == true) {
                    item { Text("Certificates", style = MaterialTheme.typography.titleLarge) }
                    items(user.certificates) { cert ->
                        CertificateItem(
                            courseTitle = cert.courseTitle,
                            onShare = { viewModel.onIntent(ProfileIntent.ShareCertificate(cert.id)) }
                        )
                    }
                }

                item {
                    Text("Preferences", style = MaterialTheme.typography.titleLarge)
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column {
                            SettingSwitch(
                                icon = Icons.Default.DarkMode,
                                title = "Dark Theme",
                                checked = state.preferences.isDarkTheme,
                                onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleDarkTheme(it)) }
                            )
                            HorizontalDivider()
                            SettingSwitch(
                                icon = Icons.Default.Notifications,
                                title = "Notifications",
                                checked = state.preferences.notificationsEnabled,
                                onCheckedChange = { viewModel.onIntent(ProfileIntent.ToggleNotifications(it)) }
                            )
                            HorizontalDivider()
                            SettingSwitch(
                                icon = Icons.Default.Wifi,
                                title = "Download over WiFi only",
                                checked = state.preferences.downloadOverWifiOnly,
                                onCheckedChange = {}
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = { viewModel.onIntent(ProfileIntent.LogoutClicked) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Logout, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Logout")
                    }
                }
            }
        }
    }
}

@Composable
private fun CertificateItem(courseTitle: String, onShare: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("🏆", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(courseTitle, style = MaterialTheme.typography.bodyMedium)
                Text("Certificate of Completion", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onShare) { Icon(Icons.Default.Share, null) }
        }
    }
}

@Composable
private fun SettingSwitch(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        leadingContent = { Icon(icon, null) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}
