package com.example.taskmanager.di

import android.app.Application
import androidx.room.Room
import com.example.taskmanager.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module   //give dagger instructions on how to create the dependencies that we need
@InstallIn(ApplicationContext::class) //used throughout the app
object AppModule {

    @Provides // we use provide method cause we dont own the classes
    @Singleton  //only one instance of task in whole app
    fun provideDatabase(
        app: Application, callback: TaskDatabase.Callback
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database") //there is a circular dependency but oncreate is called after this
        .fallbackToDestructiveMigration()
        .addCallback(callback) //di code should not be responsible for db operations
        .build()

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()

    @Provides
    @Singleton //lives as long as app lives
    fun provideApplicationScope() = CoroutineScope(SupervisorJob()) // co routine gets cancelled when child fails
}