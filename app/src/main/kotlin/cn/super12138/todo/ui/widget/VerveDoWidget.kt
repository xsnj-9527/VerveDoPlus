package cn.super12138.todo.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.ui.activities.MainActivity
import org.koin.core.context.GlobalContext

/**
 * 桌面卡片（AppWidget）的渲染与交互逻辑。
 *
 * 卡片形态参考系统「便签」的卡片：顶部是强调色图标 + 标题 + 圆形「+」按钮，
 * 下面是若干条未完成任务，点任意一条即可勾选完成。
 */
object VerveDoWidget {
    const val ACTION_REFRESH = "cn.super12138.todo.widget.ACTION_REFRESH"
    const val ACTION_TOGGLE_TASK = "cn.super12138.todo.widget.ACTION_TOGGLE_TASK"
    const val EXTRA_TASK_ID = "cn.super12138.todo.widget.EXTRA_TASK_ID"

    /** 卡片最多显示的任务条数，实际条数还会按卡片当前高度收窄 */
    private const val MAX_ROWS = 5

    /** 不显示底部「还有 N 项」时，标题栏 + 内边距占用的高度（dp） */
    private const val CHROME_DP = 62

    /** 额外显示「还有 N 项」那一行（含上方分隔线）时需要的高度（dp） */
    private const val CHROME_WITH_FOOTER_DP = 90

    /** 单条任务行的高度（dp），与 widget_vervedo_task.xml 的 minHeight 保持一致 */
    private const val ROW_DP = 28

    /**
     * 通知所有卡片实例刷新。这里只发一条广播，开销极小，
     * 因此可以在每次任务数据发生变化后直接调用。
     */
    fun requestRefresh(context: Context) {
        context.sendBroadcast(
            Intent(context, VerveDoWidgetProvider::class.java).setAction(ACTION_REFRESH)
        )
    }

    /** 读取任务数据并重新渲染所有卡片实例 */
    suspend fun render(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, VerveDoWidgetProvider::class.java)
        )
        if (appWidgetIds.isEmpty()) return

        val pendingTasks = loadPendingTasks()

        appWidgetIds.forEach { appWidgetId ->
            val heightDp = appWidgetManager.getAppWidgetOptions(appWidgetId)
                .getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

            // 装不下时，让出一行给底部「还有 N 项」提示
            val capacity = rowsThatFit(heightDp, CHROME_DP)
            val showFooter = pendingTasks.size > capacity
            val rowLimit = if (showFooter) rowsThatFit(heightDp, CHROME_WITH_FOOTER_DP) - 1
            else capacity

            appWidgetManager.updateAppWidget(
                appWidgetId,
                buildViews(
                    context = context,
                    tasks = pendingTasks.take(rowLimit.coerceIn(0, MAX_ROWS)),
                    hiddenCount = pendingTasks.size - rowLimit.coerceIn(0, MAX_ROWS),
                    showFooter = showFooter
                )
            )
        }
    }

    /** 勾选 / 取消勾选一条任务 */
    suspend fun toggleTask(context: Context, taskId: Int) {
        val repository = repository() ?: return
        val task = runCatching { repository.getTaskById(taskId) }.getOrNull() ?: return
        runCatching { repository.updateTask(task.copy(isCompleted = !task.isCompleted)) }
        render(context)
    }

    private suspend fun loadPendingTasks(): List<TaskEntity> =
        runCatching { repository()?.getPendingTasks() }.getOrNull().orEmpty()

    /**
     * Koin 在 [cn.super12138.todo.VerveDoApp] 里启动，而 Receiver 一定晚于 Application 创建，
     * 所以这里能拿到单例；取不到时安静降级，不影响桌面渲染。
     */
    private fun repository(): TaskRepository? =
        runCatching { GlobalContext.get().get<TaskRepository>() }.getOrNull()

    private fun rowsThatFit(heightDp: Int, chromeDp: Int): Int {
        if (heightDp <= 0) return MAX_ROWS // 取不到尺寸时按上限渲染
        return ((heightDp - chromeDp) / ROW_DP).coerceIn(0, MAX_ROWS)
    }

    private fun buildViews(
        context: Context,
        tasks: List<TaskEntity>,
        hiddenCount: Int,
        showFooter: Boolean
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_vervedo)

        // 标题用来打开应用；「+」直接进到「任务 → 添加任务」
        views.setOnClickPendingIntent(R.id.widget_title, openAppIntent(context, 0))
        views.setOnClickPendingIntent(R.id.widget_add, newTaskIntent(context))

        views.removeAllViews(R.id.widget_tasks)
        if (tasks.isEmpty()) {
            views.setViewVisibility(R.id.widget_empty, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.widget_empty, View.GONE)
            tasks.forEach { task ->
                val row = RemoteViews(context.packageName, R.layout.widget_vervedo_task)
                row.setTextViewText(R.id.widget_task_text, task.content)
                // 整行都可点，点击即勾选完成
                row.setOnClickPendingIntent(
                    R.id.widget_task_row,
                    toggleTaskIntent(context, task.id)
                )
                views.addView(R.id.widget_tasks, row)
            }
        }

        val showBottom = showFooter && hiddenCount > 0
        views.setViewVisibility(R.id.widget_divider, if (showBottom) View.VISIBLE else View.GONE)
        views.setViewVisibility(R.id.widget_footer, if (showBottom) View.VISIBLE else View.GONE)
        if (showBottom) {
            views.setTextViewText(
                R.id.widget_footer,
                context.getString(R.string.widget_more, hiddenCount)
            )
            views.setOnClickPendingIntent(R.id.widget_footer, openAppIntent(context, 2))
        }

        return views
    }

    private fun openAppIntent(context: Context, requestCode: Int): PendingIntent =
        PendingIntent.getActivity(
            context,
            requestCode,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    /**
     * 「+」：打开应用并直达新建任务页。
     *
     * 带 SINGLE_TOP 是为了让已在运行的实例走 [MainActivity.onNewIntent]，
     * 而不是被 CLEAR_TOP 重建——重建会丢掉用户当前页面和未保存的输入。
     */
    private fun newTaskIntent(context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            1,
            Intent(context, MainActivity::class.java).apply {
                action = Constants.ACTION_NEW_TASK
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun toggleTaskIntent(context: Context, taskId: Int): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            taskId,
            Intent(context, VerveDoWidgetProvider::class.java).apply {
                action = ACTION_TOGGLE_TASK
                putExtra(EXTRA_TASK_ID, taskId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
}
