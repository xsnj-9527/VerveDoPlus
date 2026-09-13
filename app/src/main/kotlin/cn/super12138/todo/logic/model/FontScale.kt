package cn.super12138.todo.logic.model

import androidx.annotation.StringRes
import cn.super12138.todo.R

/**
 * 应用文字大小档位。
 *
 * [value] 是最终写入 [androidx.compose.ui.unit.Density.fontScale] 的绝对值：
 * - [FOLLOW_SYSTEM]（-1f）表示跟随系统，不做任何覆写；
 * - 其余为固定缩放比例，1f 即系统标准字号。
 *
 * 之所以使用绝对值而不是“在系统字号上再乘一个系数”，是因为这样无论用户把系统字号
 * 调成多大，都能一键把应用内文字固定回可读的大小。
 */
enum class FontScale(
    val value: Float,
    @param:StringRes val nameRes: Int
) {
    // 注意：枚举常量在 companion object 初始化之前构造，所以这里只能写字面量 -1f
    FollowSystem(value = -1f, nameRes = R.string.font_scale_follow_system),
    Percent80(value = 0.8f, nameRes = R.string.font_scale_80),
    Percent85(value = 0.85f, nameRes = R.string.font_scale_85),
    Percent90(value = 0.9f, nameRes = R.string.font_scale_90),
    Percent95(value = 0.95f, nameRes = R.string.font_scale_95),
    Percent100(value = 1f, nameRes = R.string.font_scale_100),
    Percent110(value = 1.1f, nameRes = R.string.font_scale_110),
    Percent120(value = 1.2f, nameRes = R.string.font_scale_120);

    val isFollowSystem: Boolean get() = this == FollowSystem

    companion object {
        /** 跟随系统字号的哨兵值，与 [cn.super12138.todo.constants.Constants.PREF_FONT_SCALE_DEFAULT] 保持一致 */
        const val FOLLOW_SYSTEM = -1f

        fun fromFloat(float: Float) = entries.find { it.value == float } ?: FollowSystem
    }
}
