package com.github.llmaximll.todoapp.presentation.explore.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.databinding.FragmentExploreBinding
import com.github.llmaximll.todoapp.presentation.explore.viewmodel.ExploreViewModel
import com.github.llmaximll.todoapp.presentation.explore.viewmodel.FilterTasks
import com.github.llmaximll.todoapp.presentation.explore.viewmodel.TasksResult
import com.github.llmaximll.todoapp.utils.safeNavigate
import com.github.llmaximll.todoapp.utils.toListAll
import com.github.llmaximll.todoapp.utils.toListOverdue
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null

    private val binding get() = _binding!!

    private val viewModel: ExploreViewModel by viewModels()
    private val tasksAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TasksAdapter(requireContext(), ::onTaskClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        setupListeners()
        setupLists()
        viewModel.tasksResult.observe(viewLifecycleOwner, ::handleTasks)
        viewModel.filterState.observe(viewLifecycleOwner, ::handleFilterTasks)
    }

    override fun onStart() {
        super.onStart()

        viewModel.fetchData()
    }

    private fun setupListeners() {
        binding.toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.search_fragment -> {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                        duration = 500
                    }
                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                        duration = 500
                    }
                    val directions = ExploreFragmentDirections.actionExploreFragmentToSearchFragment()
                    findNavController().safeNavigate(directions)
                    true
                }
                else -> false
            }
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_ExploreFragment_to_AddFragment)
            exitTransition = MaterialElevationScale(false).apply {
                duration = 500
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = 500
            }
        }
        binding.tasksButton.setOnClickListener {
            showTasksMenu(it)
        }
    }

    private fun setupLists() {
        binding.todayRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.todayRecyclerView.adapter = tasksAdapter
    }

    private fun handleTasks(state: TasksResult) {
        when (state) {
            is TasksResult.SuccessResult -> {
                viewModel.currentList = state.result
                when (binding.tasksButton.text) {
                    getString(FilterTasks.ALL.value) -> tasksAdapter.submitList(viewModel.currentList.toListAll())
                    getString(FilterTasks.OVERDUE.value) -> tasksAdapter.submitList(viewModel.currentList.toListOverdue())
                    else -> tasksAdapter.submitList(viewModel.currentList.toListAll())
                }
            }
            is TasksResult.ErrorResult -> {

            }
            is TasksResult.EmptyResult -> {

            }
            is TasksResult.Loading -> {

            }
        }
    }

    private fun handleFilterTasks(state: FilterTasks) {
        when (state) {
            FilterTasks.ALL -> {
                binding.tasksButton.text = getString(FilterTasks.ALL.value)
                tasksAdapter.submitList(viewModel.currentList.toListAll())
            }
            FilterTasks.OVERDUE -> {
                binding.tasksButton.text = getString(FilterTasks.OVERDUE.value)
                tasksAdapter.submitList(viewModel.currentList.toListOverdue())
            }
        }
    }

    private fun showLoadingCategories() {

    }

    private fun hideLoadingCategories() {

    }

    private fun onTaskClicked(id: Long, view: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = 500
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 500
        }
        Timber.i("id=$id")
        val extras = FragmentNavigatorExtras(view to view.transitionName)
        val directions = ExploreFragmentDirections.actionExploreFragmentToDetailsFragment(id)
        findNavController().safeNavigate(directions, extras)
    }
    private fun showTasksMenu(v: View) {
        val listPopupWindow = ListPopupWindow(
            requireContext(),
            null,
            com.google.android.material.R.attr.listPopupWindowStyle
        )
        listPopupWindow.anchorView = v

        val items = listOf(
            getString(FilterTasks.ALL.value),
            getString(FilterTasks.OVERDUE.value)
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.list_popup_window_item, items)
        listPopupWindow.setAdapter(adapter)

        listPopupWindow.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> viewModel.toggleFilterState(FilterTasks.ALL)
                1 -> viewModel.toggleFilterState(FilterTasks.OVERDUE)
            }
            listPopupWindow.dismiss()
        }

        listPopupWindow.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}