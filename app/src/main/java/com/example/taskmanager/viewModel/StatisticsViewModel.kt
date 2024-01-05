package com.example.taskmanager.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.auth.AuthRepository
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDao
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val repository: AuthRepository
) : ViewModel() {

    private val firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = firebaseUser

    var totalTasks = MutableLiveData<List<Task>>()
    var completedTasks = MutableLiveData<List<Task>>()
    var taskNum = 0
    var completedTaskNum = 0

    fun computeStatistics() {
        viewModelScope.launch {
            currentUser.value?.let {
                taskDao.getAllTasksById(it.uid).first { list ->
                    taskNum = list.size
                    totalTasks.postValue(list)
                    true
                }
                taskDao.getCompletedTasks(it.uid).first { list ->
                    completedTaskNum = list.size
                    completedTasks.postValue(list)
                    true
                }
            }
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        firebaseUser.postValue(repository.getCurrentUser())
    }


}