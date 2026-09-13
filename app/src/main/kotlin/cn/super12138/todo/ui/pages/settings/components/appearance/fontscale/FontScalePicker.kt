package cn.super12138.todo.ui.pages.settings.components.appearance.fontscale

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.FontScale
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.pages.settings.components.MoreContentSettingsItem
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.toggleButtonShapesIn

@Composable
fun FontScalePicker(
    currentFontScale: FontScale,
    onFontScaleChange: (FontScale) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val fontScaleList = FontScale.entries

    MoreContentSettingsItem(
        title = stringResource(R.string.pref_font_scale),
        description = stringResource(R.string.pref_font_scale_desc),
        modifier = modifier
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding / 4)
        ) {
            fontScaleList.forEachIndexed { index, fontScale ->
                ToggleButton(
                    content = { Text(stringResource(fontScale.nameRes)) },
                    checked = currentFontScale == fontScale,
                    onCheckedChange = {
                        onFontScaleChange(fontScale)
                        VibrationUtils.performHapticFeedback(view)
                    },
                    shapes = index toggleButtonShapesIn fontScaleList,
                    colors = VerveDoDefaults.toggleButtonColors,
                    modifier = Modifier.semantics { role = Role.RadioButton }
                )
            }
        }
    }
}
