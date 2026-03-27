package com.learnpulse.presentation.profile

import com.learnpulse.domain.model.User
import com.learnpulse.domain.model.UserPreferences
import com.learnpulse.domain.repository.UserRepository
import com.learnpulse.presentation.base.BaseViewModel
import com.learnpulse.presentation.base.UiEffect
import com.learnpulse.presentation.base.UiIntent
import com.learnpulse.presentation.base.UiState
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val preferences: UserPreferences = UserPreferences(),
    val error: String? = null
) : UiState

sealed interface ProfileIntent : UiIntent {
    data object LoadProfile : ProfileIntent
    data class ToggleDarkTheme(val isDark: Boolean) : ProfileIntent
    data class ToggleNotifications(val enabled: Boolean) : ProfileIntent
    data object LogoutClicked : ProfileIntent
    data class ShareCertificate(val certificateId: String) : ProfileIntent
}

sealed interface ProfileEffect : UiEffect {
    data object NavigateToLogin : ProfileEffect
    data class ShareCertificate(val imageUrl: String) : ProfileEffect
    data class ShowMessage(val message: String) : ProfileEffect
}

class ProfileViewModel(
    private val userRepository: UserRepository
) : BaseViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    init {
        onIntent(ProfileIntent.LoadProfile)
    }

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadProfile -> loadProfile()
            is ProfileIntent.ToggleDarkTheme -> updatePreference { copy(isDarkTheme = intent.isDark) }
            is ProfileIntent.ToggleNotifications -> updatePreference { copy(notificationsEnabled = intent.enabled) }
            is ProfileIntent.LogoutClicked -> logout()
            is ProfileIntent.ShareCertificate -> {
                val cert = state.value.user?.certificates?.find { it.id == intent.certificateId }
                cert?.let { emitEffect(ProfileEffect.ShareCertificate(it.imageUrl)) }
            }
        }
    }

    private fun loadProfile() {
        screenModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            userRepository.getCurrentUser().onEach { user ->
                updateState { copy(isLoading = false, user = user, preferences = user?.preferences ?: UserPreferences()) }
            }.launchIn(screenModelScope)
        }
    }

    private fun updatePreference(update: UserPreferences.() -> UserPreferences) {
        screenModelScope.launch {
            val user = userRepository.getCurrentUser().first() ?: return@launch
            val newPrefs = user.preferences.update()
            userRepository.updateUserPreferences(user.id, newPrefs)
        }
    }

    private fun logout() {
        screenModelScope.launch {
            userRepository.logout()
            emitEffect(ProfileEffect.NavigateToLogin)
        }
    }
}
