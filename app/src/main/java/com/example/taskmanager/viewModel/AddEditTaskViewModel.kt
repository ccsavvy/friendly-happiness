package com.example.taskmanager.viewModel

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.taskmanager.ADD_TASK_RESULT_OK
import com.example.taskmanager.EDIT_TASK_RESULT_OK
import com.example.taskmanager.R
import com.example.taskmanager.auth.AuthRepository
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDao
import com.example.taskmanager.util.NotifyWork
import com.example.taskmanager.util.NotifyWork.Companion.NOTIFICATION_ID
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale.getDefault
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

    // TODO: check this and see format
//    var custCalendar = state.get<Task>("custCalendar") ?: task?.custCalendar ?: ""
//        set(value) { //setter function
//            field = value
//            state["custCalendar"] = value
//        }

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
                    Task(name = taskName.toString(), desc = taskDesc.toString(), userID = it.uid, custCalendar = System.currentTimeMillis())
                createTask(newTask)
            }
        }

//        val customTime = custCalendar.toString()
//        val currentTime = currentTimeMillis()
//        if (customTime > currentTime) {
//            val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
//            val delay = customTime - currentTime
//            Log.e("delay", "Delay: $delay")
//            scheduleNotification(delay, data)
//
//            val titleNotificationSchedule = "Schedule notification:\\u00a0"
//            val patternNotificationSchedule = "dd.MM.yy \\u00B7 HH:mm"
//
//            viewModelScope.launch {
//                addEditChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(titleNotificationSchedule + SimpleDateFormat(
//                    patternNotificationSchedule, getDefault()
//                ).format(custCalendar.time)))
//            }
//
//        } else {
//            val errorNotificationSchedule = "Notification failed"
//            //Toast.makeText(context, errorNotificationSchedule, Toast.LENGTH_LONG).show()
//        }
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
        instanceWorkManager.beginUniqueWork(
            NotifyWork.NOTIFICATION_WORK,
            ExistingWorkPolicy.REPLACE, notificationWork
        ).enqueue()
    }


    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()

        data class ShowToastMessage(val msg: String) : AddEditTaskEvent()

    }

}