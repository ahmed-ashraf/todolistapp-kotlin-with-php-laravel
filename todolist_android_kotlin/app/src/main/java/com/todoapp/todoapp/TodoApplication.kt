package com.todoapp.todoapp

import android.app.Application
import com.todoapp.todoapp.data.source.IDefaultTasksRepository
import timber.log.Timber
import timber.log.Timber.DebugTree


class TodoApplication : Application() {

    val tasksRepository: IDefaultTasksRepository
        get() = ServiceLocator.provideTasksRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}
