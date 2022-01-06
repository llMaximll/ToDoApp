package com.github.llmaximll.todoapp.presentation.explore.view

import androidx.recyclerview.widget.RecyclerView
import com.github.llmaximll.todoapp.databinding.ItemTaskBinding
import com.github.llmaximll.todoapp.domain.tasks.models.Task
import com.github.llmaximll.todoapp.utils.titlecaseFirstCharIfItIsLowercase

class TasksViewHolder(
    private val binding: ItemTaskBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task, onTaskClick: (Long) -> Unit) {
        binding.titleTextView.text = task.title.value.titlecaseFirstCharIfItIsLowercase()
        setClickListener(onTaskClick, task)
    }

    private fun setClickListener(
        onTaskClick: (Long) -> Unit,
        task: Task
    ) {
        itemView.setOnClickListener { onTaskClick(task.id) }
    }
}