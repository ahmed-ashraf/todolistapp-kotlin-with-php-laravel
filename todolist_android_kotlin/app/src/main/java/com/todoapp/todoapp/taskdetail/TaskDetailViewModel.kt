package com.todoapp.todoapp.taskdetail

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.todoapp.todoapp.Event
import com.todoapp.todoapp.R
import com.todoapp.todoapp.data.Result
import com.todoapp.todoapp.data.Result.Success
import com.todoapp.todoapp.data.Task
import com.todoapp.todoapp.data.source.DefaultTasksRepository
import com.todoapp.todoapp.data.source.IDefaultTasksRepository
import com.todoapp.todoapp.tasks.TasksViewModel
import kotlinx.coroutines.launch

/**
 * ViewModel for the Details screen.
 */
class TaskDetailViewModel(private val tasksRepository : IDefaultTasksRepository) : ViewModel() {

    private val _taskId = MutableLiveData<String>()

    private val _task = _taskId.switchMap { taskId ->
        tasksRepository.observeTask(taskId).map { computeResult(it) }
    }
    val task: LiveData<Task?> = _task

    val isDataAvailable: LiveData<Boolean> = _task.map { it != null }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _editTaskEvent = MutableLiveData<com.todoapp.todoapp.Event<Unit>>()
    val editTaskEvent: LiveData<com.todoapp.todoapp.Event<Unit>> = _editTaskEvent

    private val _deleteTaskEvent = MutableLiveData<com.todoapp.todoapp.Event<Unit>>()
    val deleteTaskEvent: LiveData<com.todoapp.todoapp.Event<Unit>> = _deleteTaskEvent

    private val _snackbarText = MutableLiveData<com.todoapp.todoapp.Event<Int>>()
    val snackbarText: LiveData<com.todoapp.todoapp.Event<Int>> = _snackbarText

    // This LiveData depends on another so we can use a transformation.
    val completed: LiveData<Boolean> = _task.map { input: Task? ->
        input?.isCompleted ?: false
    }

    fun deleteTask() = viewModelScope.launch {
        _taskId.value?.let {
            tasksRepository.deleteTask(it)
            _deleteTaskEvent.value = com.todoapp.todoapp.Event(Unit)
        }
    }

    fun editTask() {
        _editTaskEvent.value = com.todoapp.todoapp.Event(Unit)
    }

    fun setCompleted(completed: Boolean) = viewModelScope.launch {
        val task = _task.value ?: return@launch
        if (completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    fun start(taskId: String?) {
        // If we're already loading or already loaded, return (might be a config change)
        if (_dataLoading.value == true || taskId == _taskId.value) {
            return
        }
        // Trigger the load
        _taskId.value = taskId
    }

    private fun computeResult(taskResult: Result<Task>): Task? {
        return if (taskResult is Success) {
            taskResult.data
        } else {
            showSnackbarMessage(R.string.loading_tasks_error)
            null
        }
    }


    fun refresh() {
        // Refresh the repository and the task will be updated automatically.
        _task.value?.let {
            _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.refreshTask(it.id)
                _dataLoading.value = false
            }
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        _snackbarText.value = com.todoapp.todoapp.Event(message)
    }


    class TaskDetailViewModelFactory (
            private val tasksRepository: IDefaultTasksRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
                (TaskDetailViewModel(tasksRepository) as T)
    }
}
