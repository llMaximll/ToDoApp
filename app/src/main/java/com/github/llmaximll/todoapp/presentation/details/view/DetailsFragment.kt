package com.github.llmaximll.todoapp.presentation.details.view

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.llmaximll.todoapp.R
import com.github.llmaximll.todoapp.data.tasks.local.Categories
import com.github.llmaximll.todoapp.data.tasks.local.Categories.Companion.toCategory
import com.github.llmaximll.todoapp.databinding.FragmentDetailsBinding
import com.github.llmaximll.todoapp.presentation.details.viewmodel.DeleteState
import com.github.llmaximll.todoapp.presentation.details.viewmodel.DetailsViewModel
import com.github.llmaximll.todoapp.presentation.details.viewmodel.FetchDetailsState
import com.github.llmaximll.todoapp.presentation.details.viewmodel.UpdateState
import com.github.llmaximll.todoapp.utils.showErrorResId
import com.github.llmaximll.todoapp.utils.showSnackbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

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
    private var shouldInterceptBackPress = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback {
            if (shouldInterceptBackPress) {
                onBackPressed()
            } else {
                isEnabled = false
                findNavController().popBackStack()
            }
        }
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
        viewModel.updateState.observe(viewLifecycleOwner, ::handleUpdateState)
        viewModel.deleteState.observe(viewLifecycleOwner, ::handleDeleteState)
        viewModel.fetchData()

        setupAnimationLayout()
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

        val dateString = DateFormat.format("dd/MM/yyyy HH:mm", Date(viewModel.date.timeInMillis))
        binding.dateButton.text = dateString
    }

    private fun renderError() {
        view?.showSnackbar(R.string.details_fragment_error_task_loading)
    }

    private fun showLoading(show: Boolean) {
        binding.mainLinearLayout.isVisible = !show
        binding.indicator.isVisible = show
    }

    private fun setupListeners() {
        binding.toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.toolBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete_button -> {
                    showDialogDelete()
                    true
                }
                else -> false
            }
        }
        binding.dateButton.setOnClickListener {
            showDatePicker()
        }
    }

    private fun handleUpdateState(state: UpdateState) {
        when (state) {
            UpdateState.Initial -> {
                Timber.i("Update | Initial")
                binding.updateIndicator.isGone = true
            }
            UpdateState.Loading -> {
                Timber.i("Update | Loading")
                binding.updateIndicator.isVisible = true
            }
            UpdateState.Success -> {
                Timber.i("Update | Success")
                binding.updateIndicator.isGone = true
                shouldInterceptBackPress = false
                requireActivity().onBackPressed()
            }
            is UpdateState.Error -> {
                Timber.i("Update | Error")
                binding.updateIndicator.isGone = true
                view?.showSnackbar(R.string.details_fragment_error_db)
                shouldInterceptBackPress = false
                requireActivity().onBackPressed()
            }
            is UpdateState.InputError -> {
                Timber.i("Update | InputError")
                binding.updateIndicator.isGone = true
                showDialogExit()
                handleUpdateInputError(state)
            }
        }
    }

    private fun handleUpdateInputError(error: UpdateState.InputError) {
        when (error) {
            UpdateState.InputError.Title.Empty -> binding.titleInputLayout.showErrorResId(R.string.add_fragment_error_title_empty)
            UpdateState.InputError.Title.NotUnique -> binding.titleInputLayout.showErrorResId(R.string.add_fragment_error_title_not_unique)
            UpdateState.InputError.Description -> binding.descriptionInputLayout.showErrorResId(R.string.add_fragment_error_description)
        }
    }

    private fun handleDeleteState(state: DeleteState) {
        when (state) {
            DeleteState.Initial -> {
                Timber.i("Delete | Initial")
            }
            DeleteState.Loading -> {
                Timber.i("Delete | Loading")
            }
            DeleteState.Success -> {
                Timber.i("Delete | Success")
                shouldInterceptBackPress = false
                requireActivity().onBackPressed()
            }
            DeleteState.Error -> {
                Timber.i("Delete | Error")
                view?.showSnackbar(R.string.details_fragment_error_delete)
            }
        }
    }

    private fun onBackPressed() {
        if (viewModel.updateState.value == UpdateState.Success) {
            shouldInterceptBackPress = false
            requireActivity().onBackPressed()
        } else {
            viewModel.update(
                title = binding.titleEditText.text.toString(),
                description = binding.descriptionEditText.text.toString(),
                category = binding.textField.text.toString().toCategory(),
                done = false
            )
        }
    }

    private fun showDialogDelete() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.details_fragment_dialog_delete_title)
            setMessage(R.string.details_fragment_dialog_delete_message)
            setNegativeButton(R.string.details_fragment_dialog_delete_negative_button) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(R.string.details_fragment_dialog_delete_positive_button) { dialog, _ ->
                viewModel.deleteTask(requireContext())
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showDialogExit() {
        val dialog = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.details_fragment_dialog_title)
            setMessage(R.string.details_fragment_dialog_message)
            setNegativeButton(R.string.details_fragment_dialog_button_negative) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(R.string.details_fragment_dialog_button_positive) { dialog, _ ->
                shouldInterceptBackPress = false
                requireActivity().onBackPressed()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showDatePicker() {
        val customCalendar = Calendar.getInstance()
        val datePicker =
            MaterialDatePicker.Builder.datePicker().apply {
                setTitleText(R.string.add_fragment_date_picker_title)
                setSelection(viewModel.date.timeInMillis)
            }.build()
        datePicker.addOnPositiveButtonClickListener {
            val date = datePicker.selection
            if (date != null)
                customCalendar.timeInMillis = date

            viewModel.date.set(
                customCalendar.get(Calendar.YEAR),
                customCalendar.get(Calendar.MONTH),
                customCalendar.get(Calendar.DAY_OF_MONTH)
            )

            val dateString = DateFormat.format("dd/MM/yyyy HH:mm", Date(viewModel.date.timeInMillis))
            binding.dateButton.text = dateString

            showTimePicker()
        }
        datePicker.show(parentFragmentManager, null)
    }

    private fun showTimePicker() {
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val timePicker =
            MaterialTimePicker.Builder().apply {
                setTimeFormat(clockFormat)
                setHour(viewModel.date.get(Calendar.HOUR_OF_DAY))
                setMinute(viewModel.date.get(Calendar.MINUTE))
                setTitleText(R.string.add_fragment_time_picker_title)
            }.build()
        timePicker.addOnPositiveButtonClickListener {
            viewModel.date.set(if (isSystem24Hour) Calendar.HOUR_OF_DAY else Calendar.HOUR, timePicker.hour)
            viewModel.date.set(Calendar.MINUTE, timePicker.minute)

            val dateString = DateFormat.format("dd/MM/yyyy HH:mm", Date(viewModel.date.timeInMillis))
            binding.dateButton.text = dateString
        }
        timePicker.show(parentFragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}