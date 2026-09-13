package cn.super12138.todo.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.Confetti
import cn.super12138.todo.ui.navigation.TopLevelBackStack
import cn.super12138.todo.ui.navigation.TopNavigation
import cn.super12138.todo.ui.navigation.VerveDoDestinations
import cn.super12138.todo.ui.navigation.VerveDoScreen
import cn.super12138.todo.ui.theme.VerveDoTheme
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.configureEdgeToEdge
import cn.super12138.todo.utils.isDark
import com.kyant.m3color.dynamiccolor.ColorSpec
import org.koin.android.ext.android.get
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.scope.Scope

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityRetainedScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        configureEdgeToEdge()
        super.onCreate(savedInstanceState)

        val backStack: TopLevelBackStack<NavKey> = get()
        handleLaunchIntent(intent, backStack)

        setContent {
            val mainViewModel: MainViewModel = koinViewModel()

            val appearanceUiState by mainViewModel.appearanceUiState.collectAsStateWithLifecycle()
            val secureMode by mainViewModel.secureModeFlow.collectAsStateWithLifecycle(Constants.PREF_SECURE_MODE_DEFAULT)
            val hapticFeedback by mainViewModel.hapticFeedbackFlow.collectAsStateWithLifecycle(
                initialValue = Constants.PREF_HAPTIC_FEEDBACK_DEFAULT
            )
            val previewColorSystem by mainViewModel.previewColorSystemFlow.collectAsStateWithLifecycle(
                initialValue = Constants.PREF_PREVIEW_COLOR_SYSTEM_DEFAULT
            )
            val isConfettiVisible by mainViewModel.isConfettiVisible

            val navigationScaffoldState = rememberNavigationSuiteScaffoldState()
            val specVersion = remember(previewColorSystem) {
                if (previewColorSystem) ColorSpec.SpecVersion.SPEC_2025 else ColorSpec.SpecVersion.SPEC_2021
            }

            val isDark = appearanceUiState.darkMode.isDark()
            // 配置状态栏和底部导航栏的颜色（在用户切换深色模式时）
            // https://github.com/dn0ne/lotus/blob/master/app/src/main/java/com/dn0ne/player/MainActivity.kt#L266
            LaunchedEffect(appearanceUiState.darkMode) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }

            // 安全模式相关配置
            LaunchedEffect(secureMode) {
                if (secureMode) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }

            LaunchedEffect(hapticFeedback) { VibrationUtils.setEnabled(hapticFeedback) }

            // 当BackStack出现非顶层路由时，隐藏底部导航栏
            LaunchedEffect(backStack.backStack.lastOrNull()) {
                val isTopLevel =
                    backStack.backStack.lastOrNull() in VerveDoDestinations.entries.map { it.route }
                if (isTopLevel) {
                    if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Visible) navigationScaffoldState.show()
                } else {
                    if (navigationScaffoldState.currentValue != NavigationSuiteScaffoldValue.Hidden) navigationScaffoldState.hide()
                }
            }

            VerveDoTheme(
                darkTheme = isDark,
                pureBlackMode = appearanceUiState.pureBlackMode,
                style = appearanceUiState.paletteStyle,
                contrastLevel = appearanceUiState.contrastLevel,
                dynamicColor = appearanceUiState.dynamicColor,
                specVersion = specVersion,
                fontScale = appearanceUiState.fontScale
            ) {
                Surface(
                    color = VerveDoDefaults.Colors.Background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    val view = LocalView.current
                    NavigationSuiteScaffold(
                        state = navigationScaffoldState,
                        navigationSuiteItems = {
                            VerveDoDestinations.entries.forEach { destination ->
                                val selected = destination.route == backStack.topLevelKey
                                item(
                                    icon = {
                                        Crossfade(selected) {
                                            if (it) {
                                                Icon(
                                                    painter = painterResource(destination.selectedIcon),
                                                    contentDescription = null
                                                )
                                            } else {
                                                Icon(
                                                    painter = painterResource(destination.icon),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    },
                                    label = { Text(stringResource(destination.label)) },
                                    selected = selected,
                                    onClick = {
                                        VibrationUtils.performHapticFeedback(view)
                                        backStack.addTopLevel(destination.route)
                                    }
                                )
                            }
                        },
                        containerColor = VerveDoDefaults.Colors.Background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TopNavigation(
                            backStack = backStack,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Confetti(
                        visible = isConfettiVisible,
                        onVisibilityChange = { mainViewModel.setConfettiVisibility(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLaunchIntent(intent, get())
    }

    /**
     * 桌面卡片上的「+」要求直达「任务 → 添加任务」；
     * 其余入口（卡片标题等）只把应用切到前台，不改动导航。
     *
     * 跳转方式与「任务」页右下角的添加按钮保持一致：先切到「任务」这个顶层页，
     * 再在其上压入新建页，这样返回一次就停在「任务」列表。
     */
    private fun handleLaunchIntent(intent: Intent?, backStack: TopLevelBackStack<NavKey>) {
        if (intent?.action != Constants.ACTION_NEW_TASK) return
        // 消费掉该 Action，避免屏幕旋转等导致重建时重复跳转
        intent.action = null
        backStack.addTopLevel(VerveDoScreen.Tasks)
        backStack.add(VerveDoScreen.Editor.Add)
    }
}