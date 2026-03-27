package com.learnpulse

import androidx.compose.runtime.Composable
import com.learnpulse.ui.navigation.AppNavigation
import com.learnpulse.ui.theme.LearnPulseTheme

@Composable
fun LearnPulseApp(
    darkTheme: Boolean = false
) {
    LearnPulseTheme(darkTheme = darkTheme) {
        AppNavigation()
    }
}
