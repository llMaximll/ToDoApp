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
        WHERE ${TaskEntity.TITLE} LIKE :query OR
        ${TaskEntity.DESCRIPTION} LIKE :query
    """)
    suspend fun searchTasks(query: String): List<TaskEntity>?

    @Query("""
        SELECT *
        FROM ${TaskEntity.TABLE_NAME}
    """)
    suspend fun getAll(): List<TaskEntity?>?

    @Query("""
        SELECT ${TaskEntity.TITLE}
        FROM ${TaskEntity.TABLE_NAME}
    """)
    suspend fun getAllTitles(): List<String>?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tasks: List<TaskEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskEntity): Long

    @Transaction
    @Query("DELETE FROM ${TaskEntity.TABLE_NAME}")
    suspend fun deleteAll()

    @Query("""
        DELETE FROM ${TaskEntity.TABLE_NAME}
        WHERE ${TaskEntity.TASK_ID}=:id
    """)
    suspend fun delete(id: Long)
}