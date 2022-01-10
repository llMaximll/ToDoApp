package com.github.llmaximll.todoapp.presentation.details.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.databinding.FragmentDetailsBinding
import com.github.llmaximll.todoapp.presentation.details.viewmodel.DetailsViewModel
import com.github.llmaximll.todoapp.presentation.details.viewmodel.FetchDetailsState
import com.github.llmaximll.todoapp.utils.showSnackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels()
    private val args: DetailsFragmentArgs by navArgs()
    private val categoriesAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val items = listOf(
            Categories.PERSONAL.value, Categories.BUSINESS.value,
            Categories.EDUCATION.value, Categories.SCIENCE.value)
        ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskId = args.taskId
        viewModel.state.observe(viewLifecycleOwner, ::render)
        viewModel.fetchData()

        setupAnimationLayout()
        setupViews()
        setupListeners()
    }

    private fun setupAnimationLayout() {
        val taskId = args.taskId
        binding.root.transitionName = getString(R.string.shared_element) + taskId.toString()
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 500
            scrimColor = Color.TRANSPARENT
        }
    }

    private fun render(state: FetchDetailsState) {
        when (state) {
            is FetchDetailsState.Loading -> showLoading(show = true)
            is FetchDetailsState.Result -> renderResult(state)
            is FetchDetailsState.Error -> renderError()
        }
    }

    private fun renderResult(state: FetchDetailsState.Result) {
        showLoading(show = false)

        binding.titleEditText.setText(state.task.title.value)
        binding.descriptionEditText.setText(state.task.description.value)
        binding.textField.setText(state.task.category.value, false)
        (binding.textField as? AutoCompleteTextView)?.setAdapter(categoriesAdapter)
    }

    private fun renderError() {
        view?.showSnackbar(R.string.details_fragment_error_task_loading)
    }

    private fun showLoading(show: Boolean) {
        binding.mainLinearLayout.isVisible = !show
        binding.indicator.isVisible = show
    }

    private fun setupViews() {

    }

    private fun setupListeners() {
        binding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}