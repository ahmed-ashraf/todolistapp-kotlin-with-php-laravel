package com.todoapp.todoapp.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.todoapp.todoapp.EventObserver
import com.todoapp.todoapp.R
import com.todoapp.todoapp.TodoApplication
import com.todoapp.todoapp.databinding.TasksFragBinding
import com.todoapp.todoapp.util.setupRefreshLayout
import com.todoapp.todoapp.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class TasksFragment : Fragment() {

    private val viewModel by viewModels<TasksViewModel>() {
        TasksViewModel.TasksViewModelFactory(
                (requireContext().applicationContext as TodoApplication).tasksRepository
        )
    }

    private val args: TasksFragmentArgs by navArgs()

    private lateinit var viewDataBinding: TasksFragBinding

    private lateinit var listAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TasksFragBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilteringPopUpMenu()
                true
            }
            R.id.menu_refresh -> {
                viewModel.loadTasks(true)
                true
            }
            else -> false
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.tasksList)
        setupNavigation()
        setupFab()
    }

    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, com.todoapp.todoapp.EventObserver {
            openTaskDetails(it)
        })
        viewModel.newTaskEvent.observe(viewLifecycleOwner, com.todoapp.todoapp.EventObserver {
            navigateToAddNewTask()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showEditResultMessage(args.userMessage)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewModel.setFiltering(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_task_fab)?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
            .actionTasksFragmentToAddEditTaskFragment(
                null,
                resources.getString(R.string.add_task)
            )
        findNavController().navigate(action)
    }

    private fun openTaskDetails(taskId: String) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(taskId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TasksAdapter(viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
