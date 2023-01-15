package com.todoapp.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.todoapp.todoapp.MainCoroutineRule
import com.todoapp.todoapp.R
import com.todoapp.todoapp.data.Task
import com.todoapp.todoapp.data.source.FakeTestRepository
import com.todoapp.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class TasksViewModelTestFake {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private lateinit var tasksRepository: FakeTestRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var taskViewModel: TasksViewModel

    @Before
    fun setupViewModel() {

        tasksRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)

        taskViewModel = TasksViewModel(tasksRepository)
    }

    @Test
    fun addNewTask_setNewTaskEvent() {

        // When adding new task
        taskViewModel.addNewTask()

        // Then the new task event is triggered
        var value = taskViewModel.newTaskEvent.getOrAwaitValue()
        assertNotNull(value.getContentIfNotHandled())

    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // When the filter type is ALL_TASKS
        taskViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        var value = taskViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertEquals(true, value)

    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // Create an active task and add it to the repository.
        val task = Task("Title", "Description")
        tasksRepository.addTasks(task)

        // Mark the task as complete task.
        taskViewModel.completeTask(task, true)

        // Verify the task is completed.
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // Assert that the snackbar has been updated with the correct text.
        val snackbarText: com.todoapp.todoapp.Event<Int> =  taskViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }

}