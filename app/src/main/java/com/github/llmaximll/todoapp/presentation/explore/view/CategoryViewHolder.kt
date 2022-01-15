package com.github.llmaximll.todoapp.presentation.explore.view

import androidx.recyclerview.widget.RecyclerView
import com.github.llmaximll.todoapp.domain.tasks.models.Category
import com.github.llmaximll.todoapp.databinding.ItemCategoryBinding

class CategoryViewHolder(
    private val binding: ItemCategoryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(category: Category, onCategoryClick: (Long) -> Unit) {
        binding.tasksButton.text = category.tasks.size.toString()
        binding.categoriesTextView.text = category.title
        setClickListener(onCategoryClick, category)
    }

    private fun setClickListener(
        onCategoryClick: (Long) -> Unit,
        category: Category
    ) {
        itemView.setOnClickListener { onCategoryClick(category.id) }
    }
}