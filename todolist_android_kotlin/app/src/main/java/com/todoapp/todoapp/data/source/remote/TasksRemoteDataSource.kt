package com.todoapp.todoapp.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.todoapp.todoapp.data.Result
import com.todoapp.todoapp.data.Result.Error
import com.todoapp.todoapp.data.Result.Success
import com.todoapp.todoapp.data.Task
import com.todoapp.todoapp.data.source.TasksDataSource

object TasksRemoteDataSource : TasksDataSource {

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)


    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override suspend fun refreshTask(taskId: String) {
        refreshTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return observableTasks
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return observableTasks.map { tasks ->
            when (tasks) {
                is Result.Loading -> Result.Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull() { it.id == taskId }
                        ?: return@map Error(Exception("Not found"))
                    Success(task)
                }
            }
        }
    }

    override suspend fun getTasks(): Result<List<Task>> {
        val tasks = TasksApi.retrofitService.getTasks().map { Task(it.name, it.description, it.done, it.id.toString()) }
        return Success(tasks)
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TASKS_SERVICE_DATA[taskId]?.let {
            return Success(it)
        }
        return Error(Exception("Task not found"))
    }

    private fun addTask(title: String, description: String) {
        val newTask = Task(title, description)
        TASKS_SERVICE_DATA[newTask.id] = newTask
    }

    override suspend fun saveTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = task
        TasksApi.retrofitService.addTask(TaskDtoRequest(name = task.title, description = task.description))
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        TASKS_SERVICE_DATA[task.id] = completedTask
        TasksApi.retrofitService.toggleTask(task.id.toInt())
    }

    override suspend fun completeTask(taskId: String) {
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        TASKS_SERVICE_DATA[task.id] = activeTask
        TasksApi.retrofitService.toggleTask(task.id.toInt())
    }

    override suspend fun activateTask(taskId: String) {
    }

    override suspend fun clearCompletedTasks() {
        TASKS_SERVICE_DATA = TASKS_SERVICE_DATA.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
        TasksApi.retrofitService.deleteAll()
    }

    override suspend fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override suspend fun deleteTask(taskId: String) {
        TasksApi.retrofitService.deleteTask(taskId.toInt())
        TASKS_SERVICE_DATA.remove(taskId)
    }

    override suspend fun updateTask(task: Task) {
        TASKS_SERVICE_DATA[task.id] = task
        TasksApi.retrofitService.updateTask(id =  task.id.toInt() ,TaskDtoRequest(name = task.title, description = task.description))
    }
}
