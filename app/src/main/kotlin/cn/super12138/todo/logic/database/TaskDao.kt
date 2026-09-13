package cn.super12138.todo.logic.database

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import cn.super12138.todo.constants.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Query("SELECT * FROM ${Constants.DB_TABLE_NAME}")
    fun getAll(): Flow<List<TaskEntity>>

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM ${Constants.DB_TABLE_NAME} WHERE id in (:taskIds)")
    suspend fun deleteFromIds(taskIds: Set<Int>)

    /** 桌面卡片用：按“优先级高的靠前、其次添加先后顺序”取出未完成任务 */
    @Query("SELECT * FROM ${Constants.DB_TABLE_NAME} WHERE completed = 0 ORDER BY priority DESC, id ASC")
    suspend fun getPendingTasks(): List<TaskEntity>

    @Query("SELECT * FROM ${Constants.DB_TABLE_NAME} WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): TaskEntity?

    /*@Query("DELETE FROM todo")
    suspend fun deleteAllTodo()*/
}