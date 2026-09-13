package cn.super12138.todo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.FontScale
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.utils.keyColorBasedOnDynamicColor
import com.kyant.m3color.dynamiccolor.ColorSpec
import com.kyant.m3color.dynamiccolor.DynamicScheme

@Composable
fun VerveDoTheme(
    color: Color? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlackMode: Boolean = false,
    style: PaletteStyle = PaletteStyle.TonalSpot,
    contrastLevel: ContrastLevel = ContrastLevel.Default,
    dynamicColor: Boolean = true, // Dynamic color is available on Android 12+
    specVersion: ColorSpec.SpecVersion = ColorSpec.SpecVersion.SPEC_2021,
    platform: DynamicScheme.Platform = DynamicScheme.Platform.PHONE,
    animate: Boolean = true,
    fontScale: FontScale = FontScale.FollowSystem,
    content: @Composable () -> Unit
) {
    // 关键色，如果指定就使用
    val keyColor = color ?: dynamicColor.keyColorBasedOnDynamicColor()

    val colorScheme = rememberDynamicColorScheme(
        keyColor = keyColor,
        isDark = darkTheme,
        pureBlack = pureBlackMode,
        style = style,
        contrastLevel = contrastLevel.value.toDouble(),
        specVersion = specVersion,
        platform = platform
    )

    // 自定义字号：跟随系统时沿用系统 density，否则覆写 fontScale。
    // 覆写 fontScale 会统一缩放所有以 sp 计量的文字（包括写死 fontSize 的地方）。
    val systemDensity = LocalDensity.current
    val appliedDensity = remember(systemDensity, fontScale) {
        if (fontScale.isFollowSystem) systemDensity
        else Density(density = systemDensity.density, fontScale = fontScale.value)
    }

    MaterialExpressiveTheme(
        colorScheme = if (animate) animateColorScheme(colorScheme) else colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(LocalDensity provides appliedDensity) {
            content()
        }
    }
}