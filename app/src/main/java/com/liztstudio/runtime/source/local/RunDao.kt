package com.liztstudio.runtime.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunEntity)

    @Delete
    suspend fun deleteRun(run: RunEntity)

    @Query("SELECT * FROM tb_running ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<RunEntity>>

    @Query("SELECT * FROM tb_running ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<RunEntity>>

    @Query("SELECT * FROM tb_running ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<RunEntity>>

    @Query("SELECT * FROM tb_running ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<RunEntity>>

    @Query("SELECT * FROM tb_running ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<RunEntity>>

    @Query("SELECT SUM(timeInMillis) FROM tb_running")
    fun getTotalTimeInMillis(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM tb_running")
    fun getTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM tb_running")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT SUM(avgSpeedInKMH) FROM tb_running")
    fun getTotalAvgSpeed(): LiveData<Float>


}