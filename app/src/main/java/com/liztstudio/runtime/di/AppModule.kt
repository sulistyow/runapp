package com.liztstudio.runtime.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.liztstudio.runtime.source.local.RunningDatabase
import com.liztstudio.runtime.utils.Constant.KEY_FIRST_TIME_TOOGLE
import com.liztstudio.runtime.utils.Constant.KEY_NAME
import com.liztstudio.runtime.utils.Constant.KEY_WEIGHT
import com.liztstudio.runtime.utils.Constant.RUNNING_DATABASE_NAME
import com.liztstudio.runtime.utils.Constant.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.runDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) = app.getSharedPreferences(
        SHARED_PREFERENCES_NAME, MODE_PRIVATE
    )

    @Singleton
    @Provides
    fun providesName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun providesWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT, 76f)

    @Singleton
    @Provides
    fun providesFirstTimeToogle(sharedPref: SharedPreferences) = sharedPref.getBoolean(KEY_FIRST_TIME_TOOGLE, true)

}