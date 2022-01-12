package com.github.llmaximll.todoapp.presentation.explore.view

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.presentation.explore.viewmodel.ExploreViewModel
import com.google.android.material.snackbar.Snackbar

class ItemTouchHelperCallback(
    private val adapter: TasksAdapter,
    private val rootView: View,
    private val rV: RecyclerView,
    private val viewModel: ExploreViewModel
    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        val task: Task = adapter.currentList[position]

        viewModel.deleteTask(task)
        adapter.notifyItemRemoved(position)
        Snackbar.make(
            rootView,
            R.string.explore_fragment_task_deleted,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.explore_fragment_task_undo) {
            viewModel.addTask(task)
            adapter.notifyItemInserted(position)
            rV.scrollToPosition(position)
        }.show()
    }


}