package com.liztstudio.runtime.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.liztstudio.runtime.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    mainRepo: MainRepository
) : ViewModel() {

    val totalTimeRun = mainRepo.getTotalTimeInMillis()
    val totalDistance = mainRepo.getTotalDistance()
    val totalCalories = mainRepo.getTotalCaloriesBurned()
    val totalAvgSpeed = mainRepo.getTotalAvgSpeed()

    val runsSortedByDate = mainRepo.getAllRunsSortedByDate()
}