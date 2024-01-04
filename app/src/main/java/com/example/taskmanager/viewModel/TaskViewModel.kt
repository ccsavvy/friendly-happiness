package com.example.taskmanager.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.ADD_TASK_RESULT_OK
import com.example.taskmanager.EDIT_TASK_RESULT_OK
import com.example.taskmanager.auth.AuthRepository
import com.example.taskmanager.data.PreferencesManager
import com.example.taskmanager.data.SortOrder
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDao
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    private val repository: AuthRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("query", "") // how "query"?
    val preferenceFlow = preferencesManager.preferencesFlow

    private val firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = firebaseUser

    private val tasksEventChannel = Channel<TaskEvent>()
    val taskEvent = tasksEventChannel.receiveAsFlow() // receive as flow

    var tasks = MutableLiveData<List<Task>>()

    fun initTask() {
        viewModelScope.launch {

            val searchQuery = state.getLiveData("query", "") // how "query"?
            val preferenceFlow = preferencesManager.preferencesFlow

            combine(
                searchQuery.asFlow(), preferenceFlow
            ) { query, filterPreferences ->
                Pair(query, filterPreferences)
            }.flatMapLatest { (query, filterPreferences) ->
                currentUser.value?.let { user ->
                    taskDao.getTasks(
                        user.uid,
                        query,
                        filterPreferences.sortOrder,
                        filterPreferences.hideCompleted
                    )
                } ?: emptyFlow()
            }.collect { taskitem ->
                tasks.postValue(taskitem)
            }

        }
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) {
        viewModelScope.launch {
            tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskChecked(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(checked = isChecked))
        }
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmation("Task Added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmation("Task Updated")
        }
    }

    fun showTaskSavedConfirmation(message: String) = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.ShowTaskSavedConfirmation(message))
    }

    fun onDeleteAllCompleted() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }

    fun getCurrentUser() = viewModelScope.launch {
        firebaseUser.postValue(repository.getCurrentUser())
    }

    sealed class TaskEvent {  //different variation, can later get warning when the when statement is not exhaustive, there are no other kinds of task events compiler know
        object NavigateToAddTaskScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) :
            TaskEvent() // generic name cause viewmodel not sure of the view
        data class ShowTaskSavedConfirmation(val message: String) : TaskEvent()
        object NavigateToDeleteAllCompletedScreen : TaskEvent()
    }
}

