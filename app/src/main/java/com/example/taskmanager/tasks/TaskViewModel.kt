package com.example.taskmanager.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.PreferencesManager
import com.example.taskmanager.data.SortOrder
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val preferenceFlow = preferencesManager.preferencesFlow

    private val tasksFlow = combine(
        searchQuery,
        preferenceFlow
    ){
        query, filterPreferences->
            Pair(query, filterPreferences)
    }.flatMapLatest{ (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }
    val tasks = tasksFlow.asLiveData() //live data is similar to flow, live data has latest value and not whole stream of values

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }
    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }
    fun onTaskSelected(task: Task){}

    fun onTaskChecked(task: Task, isChecked: Boolean){
        viewModelScope.launch {
            taskDao.update(task.copy(checked = isChecked))
        }

    }

}

