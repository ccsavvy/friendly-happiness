package com.example.taskmanager.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    fun getTasks(userId: String, query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder){
            SortOrder.BY_DATE -> getTasksSortedByDate(userId,query,hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(userId,query, hideCompleted)
        }


    @Query("SELECT * FROM task_table WHERE (checked != :hideCompleted OR checked = 0) AND userID == :userId AND name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun getTasksSortedByName(userId: String, searchQuery: String, hideCompleted: Boolean): Flow<List<Task>> // flow is asynchronous stream of data, like live data

    @Query("SELECT * FROM task_table WHERE (checked != :hideCompleted OR checked = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun getTaskSorted(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>> // flow is asynchronous stream of data, like live data

    @Query("SELECT * FROM task_table WHERE (checked != :hideCompleted OR checked = 0) AND userID == :userId AND name LIKE '%' || :searchQuery || '%' ORDER BY created ASC")
    fun getTasksSortedByDate(userId: String, searchQuery: String, hideCompleted: Boolean): Flow<List<Task>> // flow is asynchronous stream of data, like live data
    @Query("SELECT * FROM task_table")
    fun getAllTasks(): Flow<List<Task>>
    @Query("SELECT * FROM task_table WHERE userID == :userId")
    fun getAllTasksById(userId: String): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (checked = 1) AND userID == :userId")
    fun getCompletedTasks(userId: String): Flow<List<Task>> // flow is asynchronous stream of data, like live data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task) //kotlin coroutine feature , a way to shift this function to a different thread instead of doing on the main thread
    //will do this without blocking the thread, u can call one suspend function from another suspend function or coroutines

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE checked = 1")
    suspend fun deleteCompletedTasks()

}