package com.liztstudio.runtime.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.liztstudio.runtime.R
import com.liztstudio.runtime.databinding.FragmentStatisticsBinding
import com.liztstudio.runtime.ui.viewmodels.StatisticsViewModel
import com.liztstudio.runtime.utils.CustomMarkerView
import com.liztstudio.runtime.utils.TrackingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModels()

    private lateinit var bind: FragmentStatisticsBinding

    private fun subscribeToObservers() {
        viewModel.totalTimeRun.observe(viewLifecycleOwner) {
            it?.let {
                val totalTimeRun = TrackingUtils.getFormattedTime(it)
                bind.tvTotalTime.text = totalTimeRun
            }
        }

        viewModel.totalDistance.observe(viewLifecycleOwner) {
            it?.let {
                val km = it / 1000f
                val totalDistance = round(km * 10f) / 10f
                val totalDistanceString = "${totalDistance}km"
                bind.tvTotalDistance.text = totalDistanceString
            }
        }

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner) {
            it?.let {
                val avg = round(it * 10f) / 10f
                val avgSpeed = "${avg}km/h"
                bind.tvAverageSpeed.text = avgSpeed
            }
        }

        viewModel.totalCalories.observe(viewLifecycleOwner) {
            it?.let {
                val calories = "${it}kcal"
                bind.tvTotalCalories.text = calories
            }
        }

        viewModel.runsSortedByDate.observe(viewLifecycleOwner) {
            it?.let {
                val allAvgSpeed = it.indices.map { i ->
                    BarEntry(i.toFloat(), it[i].avgSpeedInKMH)
                }
                var barDataSet = BarDataSet(allAvgSpeed, "Avg Spee Over Time").apply {
                    valueTextColor = Color.BLACK
                    color = ContextCompat.getColor(requireContext(), R.color.purple_500)
                }
                bind.barChart.data = BarData(barDataSet)
                bind.barChart.marker = CustomMarkerView(it.reversed(), requireContext())
                bind.barChart.invalidate()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentStatisticsBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()
    }

    private fun setupBarChart() {
        bind.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        bind.barChart.axisLeft.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        bind.barChart.axisRight.apply {
            axisLineColor = Color.BLACK
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        bind.barChart.apply {
            description.text = "avg Speed Over Time"
            legend.isEnabled = false

        }

    }

}