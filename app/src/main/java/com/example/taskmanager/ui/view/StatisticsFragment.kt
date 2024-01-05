package com.example.taskmanager.ui.view

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskmanager.MainActivity
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentStatisticsBinding
import com.example.taskmanager.util.StatsChart
import com.example.taskmanager.viewModel.StatisticsViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()
    lateinit var pieChart: PieChart


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getUser()
        registerObservers()
        binding = FragmentStatisticsBinding.inflate(inflater)
        pieChart = binding.pieChart
        // val progress = binding.progress
        pieChart()
        setData()
        return binding.root
    }

    fun pieChart() {
        StatsChart().setUI(pieChart)
    }

    fun setData() {
        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.grey_100))
        colors.add(resources.getColor(R.color.green_200))

        val entries: ArrayList<PieEntry> = ArrayList()

        viewModel.completedTasks.observe(viewLifecycleOwner) {
            val completedTask = viewModel.completedTaskNum.toFloat()
            val totalTask = viewModel.taskNum.toFloat()
            var num = (completedTask / totalTask * 100).toInt()
            //progress.progress = num

            entries.add(PieEntry(completedTask))
            entries.add(PieEntry(totalTask - completedTask))
            val dataSet = PieDataSet(entries, "Tasks")

            pieChart.centerText = "$num% \nCompleted"

            //  setting colors.
            dataSet.colors = colors

            //  setting pie data set
            val data = PieData(dataSet)
            pieChart.setData(data)
            pieChart.data.setDrawValues(false)
        }
    }

    fun getUser() {
        viewModel.getCurrentUser()
    }


    fun registerObservers() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                viewModel.computeStatistics()
            } ?: startActivity(Intent(requireContext(), MainActivity::class.java))
        }

        viewModel.totalTasks.observe(viewLifecycleOwner) {
            binding.apply {
                totalTasks.text = viewModel.taskNum.toString()
            }
        }
        viewModel.completedTasks.observe(viewLifecycleOwner) {
            binding.apply {
                completedTasks.text = viewModel.completedTaskNum.toString()
            }
        }
    }

}