package com.phantom.theme

import androidx.compose.ui.graphics.Color

data class PhantomTheme(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val accentSecondary: Color,
    val border: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val critical: Color,
    val info: Color,
    val debug: Color,
    val statusSuccess: Color,
    val statusClientError: Color,
    val statusServerError: Color,
    val statusPending: Color,
    val mockEnabled: Color,
    val mockDisabled: Color,
    val configModified: Color,
    val configDefault: Color,
    val searchBackground: Color
) {
    companion object {
        val default = PhantomTheme(
            background = Color(0xFF0D1117),
            surface = Color(0xFF161B22),
            surfaceSecondary = Color(0xFF21262D),
            textPrimary = Color(0xFFF0F6FC),
            textSecondary = Color(0xFF8B949E),
            textTertiary = Color(0xFF6E7681),
            accent = Color(0xFF58A6FF),
            accentSecondary = Color(0xFF1F6FEB),
            border = Color(0xFF30363D),
            success = Color(0xFF3FB950),
            warning = Color(0xFFD29922),
            error = Color(0xFFF85149),
            critical = Color(0xFFFF6B6B),
            info = Color(0xFF58A6FF),
            debug = Color(0xFF8B949E),
            statusSuccess = Color(0xFF3FB950),
            statusClientError = Color(0xFFD29922),
            statusServerError = Color(0xFFF85149),
            statusPending = Color(0xFF8B949E),
            mockEnabled = Color(0xFF3FB950),
            mockDisabled = Color(0xFF8B949E),
            configModified = Color(0xFFD29922),
            configDefault = Color(0xFF8B949E),
            searchBackground = Color(0xFF0D1117)
        )
    }
}
