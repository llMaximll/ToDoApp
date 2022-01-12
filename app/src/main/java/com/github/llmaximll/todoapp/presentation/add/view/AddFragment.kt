package com.github.llmaximll.todoapp.presentation.add.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.data.tasks.local.Categories.Companion.toCategory
import com.github.llmaximll.todoapp.databinding.FragmentAddBinding
import com.github.llmaximll.todoapp.presentation.add.viewmodel.AddState
import com.github.llmaximll.todoapp.presentation.add.viewmodel.AddViewModel
import com.github.llmaximll.todoapp.utils.showErrorResId
import com.github.llmaximll.todoapp.utils.showSnackbar
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!

    private val viewModel: AddViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupViews()
        setupObservers()
        setupAnimationLayout()
    }

    private fun setupListeners() {
        binding.addButton.setOnClickListener {
            if (viewModel.addState.value != AddState.Loading) {
                viewModel.add(
                    title = binding.titleEditText.text.toString(),
                    description = binding.descriptionEditText.text.toString(),
                    category = binding.textField.text.toString().toCategory()
                )
            }
        }
        binding.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupObservers() {
        viewModel.addState.observe(viewLifecycleOwner, ::handleAddState)
    }

    private fun handleAddState(state: AddState) {
        when (state) {
            AddState.Initial -> binding.addIndicator.isGone = true
            AddState.Loading -> binding.addIndicator.isVisible = true
            is AddState.Success -> {
                binding.addIndicator.isGone = true
                findNavController().popBackStack(R.id.explore_fragment, false)
            }
            AddState.Error -> {
                binding.addIndicator.isGone = true
                view?.showSnackbar(R.string.add_fragment_error_db)
            }
            is AddState.InputError -> {
                binding.addIndicator.isGone = true
                handleAddInputError(state)
            }
        }
    }

    private fun handleAddInputError(error: AddState.InputError) {
        when (error) {
            AddState.InputError.Title.Empty -> binding.titleInputLayout.showErrorResId(R.string.add_fragment_error_title_empty)
            AddState.InputError.Title.NotUnique -> binding.titleInputLayout.showErrorResId(R.string.add_fragment_error_title_not_unique)
            AddState.InputError.Description -> binding.descriptionInputLayout.showErrorResId(R.string.add_fragment_error_description)
        }
    }

    private fun setupViews() {
        val items = listOf(
            Categories.PERSONAL.value,
            Categories.BUSINESS.value,
            Categories.EDUCATION.value,
            Categories.SCIENCE.value,
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, items)
        (binding.textField as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setupAnimationLayout() {
        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.fab)
            endView = binding.cardView
        }
        returnTransition = Slide().apply {
            duration = 250
            addTarget(R.id.cardView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}