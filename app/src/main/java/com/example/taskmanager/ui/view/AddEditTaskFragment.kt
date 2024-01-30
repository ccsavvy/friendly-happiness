package com.example.taskmanager.ui.view


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.filepicker.FileCallback
import com.example.filepicker.FilePicker
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAddEditTaskBinding
import com.example.taskmanager.util.exhaustive
import com.example.taskmanager.viewModel.AddEditTaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {
    private val viewModel: AddEditTaskViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getUser()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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
            videoAttachment.setMediaController(MediaController(context))
            videoAttachment.requestFocus()


            // java.lang.IllegalArgumentException:
            // One of either RECEIVER_EXPORTED or RECEIVER_NOT_EXPORTED is required

            addMedia.setOnClickListener {
                FilePicker.Builder(requireActivity() as AppCompatActivity?)
                    .pick(1)
                    .anything()
                    .fromAnywhere()
                    .and(object : FileCallback {
                        override fun onFileSelected(
                            fileUri: Uri,
                            pickObject: FilePicker.PickObject
                        ) {

                            when (pickObject) {
                                FilePicker.PickObject.IMAGE -> {
                                    mediaAttachment.visibility = View.VISIBLE
                                    imgAttachment.visibility = View.VISIBLE
                                    videoAttachment.visibility = View.GONE
                                    docAttachment.visibility = View.GONE
                                    imgAttachment.setImageURI(fileUri)
                                }

                                FilePicker.PickObject.VIDEO -> {
                                    mediaAttachment.visibility = View.VISIBLE
                                    imgAttachment.visibility = View.GONE
                                    videoAttachment.visibility = View.VISIBLE
                                    docAttachment.visibility = View.GONE
                                    videoAttachment.setVideoURI(fileUri)
                                    videoAttachment.seekTo(1)
                                }

                                FilePicker.PickObject.FILE -> {
                                    mediaAttachment.visibility = View.VISIBLE
                                    docAttachment.visibility = View.VISIBLE
                                    imgAttachment.visibility = View.GONE
                                    videoAttachment.visibility = View.GONE

                                    docAttachment.fromUri(fileUri).load()
                                }

                                FilePicker.PickObject.ANY_THING -> {
                                    //Ignore
                                }
                            }
                        }

                        override fun onOperationCancelled(e: String?) {
                            Toast.makeText(
                                requireContext(),
                                e,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                    .now()
            }
            saveButton.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
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

    fun getUser() {
        viewModel.getCurrentUser()
    }


}