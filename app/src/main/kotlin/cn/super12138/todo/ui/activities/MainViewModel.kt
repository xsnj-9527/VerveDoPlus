package cn.super12138.todo.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.super12138.todo.logic.SettingsRepository
import cn.super12138.todo.ui.pages.settings.SettingsAppearanceUiState
import cn.super12138.todo.utils.ConfettiController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val confettiController: ConfettiController
) : ViewModel() {
    val isConfettiVisible = confettiController.visible
    val secureModeFlow = settingsRepository.secureModeFlow
    val hapticFeedbackFlow = settingsRepository.hapticFeedbackFlow
    val previewColorSystemFlow = settingsRepository.previewColorSystemFlow

    // combine 的具名重载最多只到 5 个流，所以分两段合并
    private val appearanceBaseFlow = combine(
        settingsRepository.dynamicColorFlow,
        settingsRepository.paletteStyleFlow,
        settingsRepository.darkModeFlow,
        settingsRepository.pureBlackFlow,
        settingsRepository.contrastLevelFlow
    ) { dynamicColor, paletteStyle, darkMode, pureBlackMode, contrastLevel ->
        SettingsAppearanceUiState(
            dynamicColor = dynamicColor,
            paletteStyle = paletteStyle,
            darkMode = darkMode,
            pureBlackMode = pureBlackMode,
            contrastLevel = contrastLevel
        )
    }

    val appearanceUiState: StateFlow<SettingsAppearanceUiState> = combine(
        appearanceBaseFlow,
        settingsRepository.fontScaleFlow
    ) { uiState, fontScale ->
        uiState.copy(fontScale = fontScale)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsAppearanceUiState()
    )

    fun setConfettiVisibility(visible: Boolean) = confettiController.setVisibility(visible)
}