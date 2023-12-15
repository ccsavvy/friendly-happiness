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
    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder){
            SortOrder.BY_DATE -> getTasksSortedByDate(query,hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (checked != :hideCompleted OR checked = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY name ASC")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>> // flow is asynchronous stream of data, like live data

    @Query("SELECT * FROM task_table WHERE (checked != :hideCompleted OR checked = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY created ASC")
    fun getTasksSortedByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>> // flow is asynchronous stream of data, like live data

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task) //kotlin coroutine feature , a way to shift this function to a different thread instead of doing on the main thread
    //will do this without blocking the thread, u can call one suspend function from another suspend function or coroutines

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

}