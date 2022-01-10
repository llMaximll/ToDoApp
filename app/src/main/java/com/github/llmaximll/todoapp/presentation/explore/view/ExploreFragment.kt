package com.github.llmaximll.todoapp.presentation.explore.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.databinding.FragmentExploreBinding
import com.github.llmaximll.todoapp.presentation.explore.viewmodel.CategoriesResult
import com.github.llmaximll.todoapp.presentation.explore.viewmodel.ExploreViewModel
import com.github.llmaximll.todoapp.presentation.explore.viewmodel.TasksResult
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
    private val categoriesAdapter = CategoriesAdapter { id ->

    }
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
        viewModel.categoriesResult.observe(viewLifecycleOwner, ::handleCategories)
        viewModel.tasksResult.observe(viewLifecycleOwner, ::handleTasks)
    }

    override fun onStart() {
        super.onStart()

        viewModel.fetchData()
    }

    private fun setupListeners() {
        binding.toolBar.setNavigationOnClickListener {
            requireActivity().findViewById<DrawerLayout>(R.id.drawerLayout).open()
        }
        binding.toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.search_fragment -> {
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                        duration = 500
                    }
                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                        duration = 500
                    }
                    findNavController().navigate(R.id.action_ExploreFragment_to_SearchFragment)
                    true
                }
                R.id.notifications_fragment -> {
                    findNavController().navigate(R.id.action_ExploreFragment_to_NotificationsFragment)
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
    }

    private fun setupLists() {
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecyclerView.adapter = categoriesAdapter

        binding.todayRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.todayRecyclerView.adapter = tasksAdapter
    }

    private fun handleCategories(state: CategoriesResult) {
        when (state) {
            is CategoriesResult.SuccessResult -> {
                categoriesAdapter.submitList(state.result)
            }
            is CategoriesResult.ErrorResult -> {

            }
            is CategoriesResult.EmptyResult -> {

            }
            is CategoriesResult.Loading -> {

            }
        }
    }

    private fun handleTasks(state: TasksResult) {
        when (state) {
            is TasksResult.SuccessResult -> {
                Timber.i("state.result=${state.result}")
                tasksAdapter.submitList(state.result)
            }
            is TasksResult.ErrorResult -> {

            }
            is TasksResult.EmptyResult -> {

            }
            is TasksResult.Loading -> {

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
        val extras = FragmentNavigatorExtras(view to view.transitionName)
        val directions = ExploreFragmentDirections.actionExploreFragmentToDetailsFragment(id)
        findNavController().navigate(directions, extras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}