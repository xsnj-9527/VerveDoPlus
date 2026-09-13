package cn.super12138.todo.logic

import cn.super12138.todo.logic.database.TaskDao
import cn.super12138.todo.logic.database.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * @param onTasksChanged 任务数据发生变化后的回调。用于通知桌面卡片刷新，
 *   以回调形式注入，避免 logic 层直接依赖 ui 层。
 */
class TaskRepository(
    private val taskDao: TaskDao,
    private val onTasksChanged: () -> Unit = {}
) {
    suspend fun insertTask(task: TaskEntity) {
        taskDao.insert(task)
        onTasksChanged()
    }

    fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAll()

    /** 桌面卡片用：未完成任务，优先级高的靠前 */
    suspend fun getPendingTasks(): List<TaskEntity> = taskDao.getPendingTasks()

    suspend fun getTaskById(id: Int): TaskEntity? = taskDao.getById(id)

    suspend fun updateTask(task: TaskEntity) {
        taskDao.update(task)
        onTasksChanged()
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
        onTasksChanged()
    }

    suspend fun deleteTaskFromIds(tasks: Set<Int>) {
        taskDao.deleteFromIds(tasks)
        onTasksChanged()
    }

    /*suspend fun deleteAllTodo() {
        toDoDao.deleteAllTodo()
    }*/
}