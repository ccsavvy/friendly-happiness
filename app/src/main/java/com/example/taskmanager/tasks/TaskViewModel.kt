package com.example.taskmanager.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.taskmanager.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import kotlin.Triple

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    private val tasksFlow = combine(
        searchQuery,
        sortOrder,
        hideCompleted
    ){
        query, sortOrder, hideCompleted ->
            Triple(query, sortOrder, hideCompleted)
    }.flatMapLatest{ (query, sortOrder, hideCompleted) ->
        taskDao.getTasks(query, sortOrder, hideCompleted)
    }

    val tasks = tasksFlow.asLiveData() //live data is similar to flow, live data has latest value and not whole stream of values

}

enum class SortOrder {
    BY_NAME, BY_DATE
}