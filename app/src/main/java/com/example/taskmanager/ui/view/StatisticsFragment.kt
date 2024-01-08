package com.example.taskmanager.ui.view

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
        binding = FragmentStatisticsBinding.inflate(inflater)
        pieChart = binding.pieChart
        getUser()
        registerObservers()
        return binding.root
    }

    fun initPieChart() {
        StatsChart().setUI(pieChart)
    }

    fun setChartData() {
        pieChart.centerText = viewModel.centerText
        pieChart.data = viewModel.data.value
        pieChart.data.setDrawValues(false)
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
        viewModel.data.observe(viewLifecycleOwner) {
            binding.apply {
                initPieChart()
                setChartData()
            }
        }
    }
}