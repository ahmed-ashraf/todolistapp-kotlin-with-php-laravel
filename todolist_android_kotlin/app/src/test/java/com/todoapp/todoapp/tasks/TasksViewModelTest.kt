package com.todoapp.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.todoapp.todoapp.getOrAwaitValue
import com.todoapp.todoapp.ServiceLocator
import com.todoapp.todoapp.data.source.IDefaultTasksRepository
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class TasksViewModelTest {

    // instant task executor rule - junit rules
    // rules run before and after each run
    // dependency: testImplementation "androidx.arch.core:core-testing:$archTestingVersion"
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var taskViewModel: TasksViewModel
    private lateinit var tasksRepository: IDefaultTasksRepository

    @Before
    fun setupViewModel() {
        // how to get application context from text test class
        // ApplicationProvider.getApplicationContext()

        tasksRepository = ServiceLocator.provideTasksRepository(ApplicationProvider.getApplicationContext())

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

        // Given a fresh ViewModel
        val taskViewModel = TasksViewModel(tasksRepository)

        // When the filter type is ALL_TASKS
        taskViewModel.setFiltering(TasksFilterType.ALL_TASKS)

        // Then the "Add task" action is visible
        var value = taskViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertEquals(true, value)

    }

}