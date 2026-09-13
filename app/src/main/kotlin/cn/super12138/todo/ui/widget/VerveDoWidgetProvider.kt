package cn.super12138.todo.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * VerveDo 桌面卡片。渲染细节都在 [VerveDoWidget] 里，
 * 这里只负责把广播转成一次异步渲染。
 */
class VerveDoWidgetProvider : AppWidgetProvider() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        renderAsync(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            // 应用内任务数据变化后发来的刷新请求
            VerveDoWidget.ACTION_REFRESH -> renderAsync(context)

            // 点击卡片上的某一条任务：切换完成状态后再刷新
            VerveDoWidget.ACTION_TOGGLE_TASK -> {
                val taskId = intent.getIntExtra(VerveDoWidget.EXTRA_TASK_ID, -1)
                if (taskId < 0) return
                val pendingResult = runCatching { goAsync() }.getOrNull()
                scope.launch {
                    try {
                        VerveDoWidget.toggleTask(context, taskId)
                    } catch (t: Throwable) {
                        Log.e(TAG, "切换任务完成状态失败", t)
                    } finally {
                        pendingResult?.finish()
                    }
                }
            }
        }
    }

    /**
     * 广播接收器的存活时间很短，用 goAsync() 把这段时间借出来给协程用，
     * 否则读取数据库的中途进程可能被回收。取不到 PendingResult 时也照样执行。
     */
    private fun renderAsync(context: Context) {
        val pendingResult = runCatching { goAsync() }.getOrNull()
        scope.launch {
            try {
                VerveDoWidget.render(context)
            } catch (t: Throwable) {
                Log.e(TAG, "刷新桌面卡片失败", t)
            } finally {
                pendingResult?.finish()
            }
        }
    }

    private companion object {
        const val TAG = "VerveDoWidget"
    }
}
