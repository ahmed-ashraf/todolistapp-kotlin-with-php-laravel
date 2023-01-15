package com.todoapp.todoapp

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.todoapp.todoapp.data.source.DefaultTasksRepository
import com.todoapp.todoapp.data.source.IDefaultTasksRepository
import com.todoapp.todoapp.data.source.TasksDataSource
import com.todoapp.todoapp.data.source.local.TasksLocalDataSource
import com.todoapp.todoapp.data.source.local.ToDoDatabase
import com.todoapp.todoapp.data.source.remote.TasksRemoteDataSource
import kotlinx.coroutines.runBlocking

/* Object class is singleton class
* A service locator for the [DefaultTasksRepository]
* */
object ServiceLocator {

    private var database: ToDoDatabase? = null

    @Volatile
    var tasksRepository: IDefaultTasksRepository? = null
        @VisibleForTesting set

    private val lock = Any()

    fun provideTasksRepository(context: Context): IDefaultTasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTaskRepository(context)
        }

    }

    private fun createTaskRepository(context: Context): DefaultTasksRepository {
        val newRepo = DefaultTasksRepository(TasksRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepo
        return newRepo
    }

    private fun createTaskLocalDataSource(context: Context): TasksDataSource {
        val database = database ?: createDataBase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDataBase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
                context.applicationContext,
                ToDoDatabase::class.java, "Tasks.db"
        ).build()
        database = result
        return result
    }

    /**
     * Reset the repository tasks and data.
     * Used for testing to clean the repository between each test
     */
    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TasksRemoteDataSource.deleteAllTasks()
            }
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }
}