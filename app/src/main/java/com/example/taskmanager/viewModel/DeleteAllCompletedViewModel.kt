package com.example.taskmanager.viewModel

import androidx.lifecycle.ViewModel
import com.example.taskmanager.data.TaskDao
import com.example.taskmanager.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
): ViewModel() {

    fun deleteAllCompletedTasks() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }
}