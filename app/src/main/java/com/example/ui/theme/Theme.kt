package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VoiceShieldColorScheme = darkColorScheme(
    primary = CyberBlue,
    onPrimary = Color(0xFF041E34),
    primaryContainer = CyberBlueContainer,
    onPrimaryContainer = Color(0xFFBAE6FD),
    
    secondary = CyberRed,
    onSecondary = Color(0xFF3F040A),
    secondaryContainer = CyberRedContainer,
    onSecondaryContainer = Color(0xFFFFD1D6),
    
    tertiary = CyberGreen,
    onTertiary = Color(0xFF003822),
    tertiaryContainer = CyberGreenContainer,
    onTertiaryContainer = Color(0xFFA7F3D0),
    
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    
    error = CyberRed,
    onError = Color.White,
    errorContainer = CyberRedContainer,
    onErrorContainer = CyberRedGlow,
    
    outline = CyberBorder,
    outlineVariant = CyberSurfaceVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force cybersecurity dark aesthetic
    dynamicColor: Boolean = false, // Keep intentional cybersecurity identity
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VoiceShieldColorScheme,
        typography = Typography,
        content = content
    )
}
