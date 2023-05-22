package com.liztstudio.runtime.repositories

import com.liztstudio.runtime.source.local.RunDao
import com.liztstudio.runtime.source.local.RunEntity
import javax.inject.Inject

class MainRepository @Inject constructor(
    val runDao: RunDao
) {
    suspend fun insertRun(run: RunEntity) = runDao.insertRun(run)
    suspend fun deleteRun(run: RunEntity) = runDao.deleteRun(run)

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()
    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()
    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()
    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()

    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()


}