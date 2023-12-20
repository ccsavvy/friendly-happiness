package com.example.taskmanager.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDao

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val state: SavedStateHandle // stores navigation arguments and information
): ViewModel() { //process dev, in case of process being stopped or destroyed we add savedinstances

    val task = state.get<Task>("task")
    var taskName = state.get<Task>("taskName")?: task?.name ?: ""
        set(value){ //setter function
            field = value
            state["taskName"] = value
        }

    var taskDesc = state.get<Task>("taskDesc")?: task?.desc ?: ""
        set(value){ //setter function
            field = value
            state["taskDesc"] = value
        }

}