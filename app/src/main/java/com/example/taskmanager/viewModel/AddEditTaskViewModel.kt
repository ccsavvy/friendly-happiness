package com.example.taskmanager.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.taskmanager.ADD_TASK_RESULT_OK
import com.example.taskmanager.EDIT_TASK_RESULT_OK
import com.example.taskmanager.auth.AuthRepository
import com.example.taskmanager.di.Task
import com.example.taskmanager.data.TaskDao
import com.example.taskmanager.util.NotifyWork
import com.example.taskmanager.util.NotifyWork.Companion.NOTIFICATION_ID
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val repository: AuthRepository,
    private val state: SavedStateHandle // stores navigation arguments and information
) : ViewModel() { //process dev, in case of process being stopped or destroyed we add savedinstances

    val task = state.get<Task>("task")

    private val firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = firebaseUser
    var taskName = state.get<Task>("taskName") ?: task?.name ?: ""
        set(value) { //setter function
            field = value
            state["taskName"] = value
        }

    var taskDesc = state.get<Task>("taskDesc") ?: task?.desc ?: ""
        set(value) { //setter function
            field = value
            state["taskDesc"] = value
        }

    var custCalendar = System.currentTimeMillis()

    private val addEditChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.toString().isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }
        if (task != null) {
            val updatedTask = task.copy(name = taskName.toString(), desc = taskDesc.toString())
            updateTask(updatedTask)
        } else {
            currentUser.value?.let {
                val newTask =
                    Task(
                        name = taskName.toString(),
                        desc = taskDesc.toString(),
                        userID = it.uid,
                        custCalendar = null
                    )
                createTask(newTask)
            }
        }

        val customTime = custCalendar
        val currentTime = currentTimeMillis()
        if (customTime > currentTime) {

            //TODO: Notification ID check
            val data = Data.Builder().putInt(NOTIFICATION_ID, 0)
                .putString(NotifyWork.NOTIFICATION_TITLE, taskName.toString())
                .build()
            val delay = customTime - currentTime
            Log.e("delay", "Delay: $delay")
            scheduleNotification(delay, data)

            val titleNotificationSchedule = "Schedule notification:\\u00a0"

            viewModelScope.launch {
                addEditChannel.send(
                    AddEditTaskEvent.ShowToastMessage(
                        titleNotificationSchedule
                    )
                )
            }

        } else {
            val errorNotificationSchedule = "Notification failed"
            viewModelScope.launch {
                addEditChannel.send(
                    AddEditTaskEvent.ShowToastMessage(
                        errorNotificationSchedule
                    )
                )
            }
        }
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        navigateWithMessage(EDIT_TASK_RESULT_OK)
    }

    fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        navigateWithMessage(ADD_TASK_RESULT_OK)
    }

    fun showInvalidInputMessage(message: String) = viewModelScope.launch {
        addEditChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(message))
    }

    fun navigateWithMessage(result: Int) = viewModelScope.launch {
        addEditChannel.send(AddEditTaskEvent.NavigateBackWithResult(result))
    }

    fun getCurrentUser() = viewModelScope.launch {
        firebaseUser.postValue(repository.getCurrentUser())
    }


    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyWork::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        val instanceWorkManager = WorkManager.getInstance()
        instanceWorkManager.beginWith(
            notificationWork
        ).enqueue()
    }


    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
        data class ShowToastMessage(val msg: String) : AddEditTaskEvent()

    }

}