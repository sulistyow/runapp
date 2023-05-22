package com.liztstudio.runtime.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liztstudio.runtime.repositories.MainRepository
import com.liztstudio.runtime.source.local.RunEntity
import com.liztstudio.runtime.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mainRepo: MainRepository
) : ViewModel() {

    private val runSortedByDate = mainRepo.getAllRunsSortedByDate()
    private val runSortedByDistance = mainRepo.getAllRunsSortedByDistance()
    private val runSortedByCalories = mainRepo.getAllRunsSortedByCaloriesBurned()
    private val runSortedByTimeMilis = mainRepo.getAllRunsSortedByTimeInMillis()
    private val runSortedBySpeed = mainRepo.getAllRunsSortedByAvgSpeed()

    val runs = MediatorLiveData<List<RunEntity>>()

    var sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                result.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                result.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runSortedByCalories) { result ->
            if (sortType == SortType.CALORIES_BURNED) {
                result.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runSortedByTimeMilis) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                result.let {
                    runs.value = it
                }
            }
        }

        runs.addSource(runSortedBySpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                result.let {
                    runs.value = it
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when (sortType) {
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByTimeMilis.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCalories.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedBySpeed.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
    }.also { this.sortType = sortType }

    fun insertRun(runEntity: RunEntity) = viewModelScope.launch {
        mainRepo.insertRun(runEntity)
    }
}