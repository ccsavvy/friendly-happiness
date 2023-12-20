package com.example.taskmanager.ui.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAddEditTaskBinding
import com.example.taskmanager.util.exhaustive
import com.example.taskmanager.viewModel.AddEditTaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment: Fragment(R.layout.fragment_add_edit_task) {
    private val viewModel: AddEditTaskViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditTaskBinding.bind(view)
        binding.apply {
            name.setText(viewModel.taskName.toString())
            desc.setText(viewModel.taskDesc.toString())

            dateCreated.isVisible = viewModel.task != null
            dateCreated.text = "Created: ${viewModel.task?.createDateFormatted}"

            name.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            desc.addTextChangedListener {
                viewModel.taskDesc = it.toString()
            }
            saveButton.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when(event){
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.name.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }

                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage ->
                        Snackbar.make(view, event.msg, Snackbar.LENGTH_LONG).show()
                }.exhaustive
            }
        }
    }


}