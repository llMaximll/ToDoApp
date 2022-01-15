package com.github.llmaximll.todoapp.presentation.search.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.databinding.FragmentSearchBinding
import com.github.llmaximll.todoapp.presentation.explore.view.TasksAdapter
import com.github.llmaximll.todoapp.presentation.search.viewmodel.SearchResult
import com.github.llmaximll.todoapp.presentation.search.viewmodel.SearchViewModel
import com.github.llmaximll.todoapp.utils.afterTextChanged
import com.github.llmaximll.todoapp.utils.showKeyboard
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val tasksAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TasksAdapter(requireContext(), ::onTaskClicked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupAnimationLayout()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        setupListeners()
        setupTasksList()
        binding.searchEditText.showKeyboard()
        binding.searchEditText.afterTextChanged(viewModel::onNewQuery)
        viewModel.searchResult.observe(viewLifecycleOwner, ::handleTasksList)
    }

    private fun setupListeners() {
        binding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupTasksList() {
        binding.tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tasksRecyclerView.adapter = tasksAdapter
    }

    private fun handleTasksList(state: SearchResult) {
        when (state) {
            is SearchResult.SuccessResult -> {
                hideLoading()
                binding.placeholderLinearLayout.isGone = true
                binding.tasksRecyclerView.isVisible = true
                tasksAdapter.submitList(state.result)
            }
            is SearchResult.ErrorResult -> {
                hideLoading()
                hideAndSetEmptyList()
                binding.placeholderLinearLayout.isVisible = true
                binding.placeholderTextView.setText(R.string.search_fragment_error_search)
                Timber.e("Something went wrong.", state.e)
            }
            is SearchResult.EmptyResult -> {
                hideLoading()
                hideAndSetEmptyList()
                binding.placeholderLinearLayout.isVisible = true
                binding.placeholderTextView.setText(R.string.search_fragment_nothing_found)
            }
            is SearchResult.EmptyQuery -> {
                hideLoading()
                hideAndSetEmptyList()
                binding.placeholderLinearLayout.isVisible = true
                binding.placeholderTextView.setText(R.string.search_fragment_enter_something)
            }
            is SearchResult.Loading -> {
                showLoading()
            }
        }
    }

    private fun hideAndSetEmptyList() {
        tasksAdapter.submitList(emptyList())
        binding.placeholderLinearLayout.isVisible = true
        binding.tasksRecyclerView.isGone = true
    }

    private fun showLoading() {
        binding.searchImageView.isGone = true
        binding.searchProgressIndicator.isVisible = true
    }

    private fun hideLoading() {
        binding.searchProgressIndicator.isGone = true
        binding.searchImageView.isVisible = true
    }

    private fun setupAnimationLayout() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = 500
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = 500
        }
    }

    private fun onTaskClicked(id: Long, view: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = 500
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 500
        }
        val extras = FragmentNavigatorExtras(view to view.transitionName)
        val directions = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(id)
        findNavController().navigate(directions, extras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}