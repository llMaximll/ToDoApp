package com.github.llmaximll.todoapp.presentation.explore.view

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.databinding.ItemTaskBinding
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.utils.titlecaseFirstCharIfItIsLowercase
import timber.log.Timber

class TasksViewHolder(
    private val binding: ItemTaskBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(context: Context, task: Task, onTaskClick: (Long, View) -> Unit) {
        binding.titleTextView.text = task.title.value.titlecaseFirstCharIfItIsLowercase()
        itemView.transitionName = context.resources.getString(R.string.shared_element) + task.id
        setClickListener(onTaskClick, task)

        Timber.i("Explore | transitionName = ${itemView.transitionName}")
    }

    private fun setClickListener(
        onTaskClick: (Long, View) -> Unit,
        task: Task
    ) {
        itemView.setOnClickListener { onTaskClick(task.id, itemView) }
    }
}