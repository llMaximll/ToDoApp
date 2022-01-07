package com.github.llmaximll.todoapp.presentation.add.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.data.tasks.local.Categories.Companion.toCategory
import com.github.llmaximll.todoapp.databinding.FragmentAddBinding
import com.github.llmaximll.todoapp.presentation.add.viewmodel.AddState
import com.github.llmaximll.todoapp.presentation.add.viewmodel.AddViewModel
import com.github.llmaximll.todoapp.utils.showErrorResId
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
    }

    private fun setupListeners() {
        binding.addButton.setOnClickListener {
            viewModel.add(
                title = binding.titleEditText.text.toString(),
                description = binding.titleEditText.text.toString(),
                category = binding.textField.text.toString().toCategory()
            )
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
            AddState.Loading -> Unit
            AddState.Success -> findNavController().popBackStack()
            is AddState.InputError -> handleAddInputError(state)
        }
    }

    private fun handleAddInputError(error: AddState.InputError) {
        when (error) {
            AddState.InputError.Title -> binding.titleInputLayout.showErrorResId(R.string.add_fragment_error_title)
            AddState.InputError.Description -> Unit
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}