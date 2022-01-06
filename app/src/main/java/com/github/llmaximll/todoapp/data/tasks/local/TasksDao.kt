package com.github.llmaximll.todoapp.data.tasks.local

import androidx.room.*

@Dao
interface TasksDao {

    @Query("""
        SELECT *
        FROM ${TaskEntity.TABLE_NAME}
        WHERE ${TaskEntity.TASK_ID}=:id
        LIMIT 1
    """)
    suspend fun getById(id: Long): TaskEntity?

    @Query("""
        SELECT *
        FROM ${TaskEntity.TABLE_NAME}
    """)
    suspend fun getAll(): List<TaskEntity?>?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Transaction
    @Query("DELETE FROM ${TaskEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("""
        DELETE FROM ${TaskEntity.TABLE_NAME}
        WHERE ${TaskEntity.TASK_ID}=:id
    """)
    suspend fun delete(id: Long)
}