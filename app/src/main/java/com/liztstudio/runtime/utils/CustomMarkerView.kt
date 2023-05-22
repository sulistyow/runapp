package com.liztstudio.runtime.utils

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.liztstudio.runtime.R
import com.liztstudio.runtime.source.local.RunEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomMarkerView(
    val runs: List<RunEntity>,
    c: Context,
    layoutId: Int = R.layout.marker_view
) : MarkerView(c, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        val avgSpeed = "${run.avgSpeedInKMH}km/h"
        val distanceKm = "${run.distanceInMeters / 1000f}km"
        val caloriesBurned = "${run.caloriesBurned}kcal"

        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvAvgSpeed = findViewById<TextView>(R.id.tvAvgSpeed)
        val tvDistance = findViewById<TextView>(R.id.tvDistance)
        val tvTime = findViewById<TextView>(R.id.tvDuration)
        val tvCalories = findViewById<TextView>(R.id.tvCaloriesBurned)

        tvDate.text = dateFormat.format(calendar.time)
        tvAvgSpeed.text = avgSpeed
        tvDistance.text = distanceKm
        tvTime.text = TrackingUtils.getFormattedTime(run.timeInMillis)
        tvCalories.text = caloriesBurned
    }
}