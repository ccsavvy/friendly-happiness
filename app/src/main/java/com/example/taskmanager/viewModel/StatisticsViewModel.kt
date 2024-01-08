package com.example.taskmanager.viewModel

import android.app.Application
import android.graphics.Color
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.R
import com.example.taskmanager.auth.AuthRepository
import com.example.taskmanager.data.Task
import com.example.taskmanager.data.TaskDao
import com.example.taskmanager.di.ApplicationScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val repository: AuthRepository,
    ) : ViewModel() {

    private val firebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = firebaseUser

    var centerText: String = ""

    var totalTasks = MutableLiveData<List<Task>>()
    var completedTasks = MutableLiveData<List<Task>>()
    var taskNum = 0
    var completedTaskNum = 0
    var data = MutableLiveData<PieData>()

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
                setChartData()
            }
        }
    }

    fun setChartData() {
        val entries: ArrayList<PieEntry> = ArrayList()
        val completedTask = completedTaskNum.toFloat()
        val totalTask = taskNum.toFloat()
        val num = (completedTask / totalTask * 100).toInt()

        entries.add(PieEntry(completedTask))
        entries.add(PieEntry(totalTask - completedTask))
        var dataSet = PieDataSet(entries, "Tasks")

        centerText = "$num% \nCompleted"

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.rgb(187, 134, 252))
        colors.add(Color.rgb(116, 116, 116))
        colors.add(R.color.green_200)

        dataSet.colors = colors
        data.postValue(PieData(dataSet))

    }

    fun getCurrentUser() = viewModelScope.launch {
        firebaseUser.postValue(repository.getCurrentUser())
    }
}