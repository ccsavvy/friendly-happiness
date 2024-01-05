package com.example.taskmanager.util

import android.graphics.Typeface
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart

class StatsChart() {
    fun setUI(pieChart: PieChart){
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)


        //  setting animation for our pie chart
        pieChart.animateY(500, Easing.EaseInCirc)


        // setting center text
        pieChart.setDrawCenterText(true)
        pieChart.setCenterTextSize(25f)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)


        //  disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false

    }




}