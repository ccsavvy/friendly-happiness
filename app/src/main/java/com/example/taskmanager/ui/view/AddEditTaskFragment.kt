package com.example.taskmanager.ui.view


import android.R.attr.data
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.example.taskmanager.util.filepicker.FilePicker
import com.example.taskmanager.viewModel.AddEditTaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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

            /**
             * ToDo:
             * Feel free to change the design and behavior.
             * Also if you are moving code to ViewModel as we discussed earlier,
             * you can move this click listener as well.
             * */


            val launcher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { //type of result here ActivityResultContracts.StartActivityForResult- is used for launching activities and getting results.
                    val uri = it.data?.data
                    binding.imgAttachment.setImageURI(uri)
                    Log.e("URI",uri.toString())

                    /**
                     * ToDo:
                     * Need to check by the uri of image using the putExtra
                     */
                    //If picture taken from camera
                    val photo = it.data?.extras?.get("data") as Bitmap?
                    binding.imgAttachment.setImageBitmap(photo)

//                    val isCameraResult = it.data?.hasExtra(MediaStore.EXTRA_OUTPUT) == true
//
//                    if (isCameraResult) {
//                        val fileUri = uri
//                        binding.imgAttachment.setImageURI(fileUri)
//                    } else {
//                        binding.imgAttachment.setImageURI(uri)
//                    }
                }

            imgAttachment.setOnClickListener {
                FilePicker.Builder(requireActivity() as AppCompatActivity?, launcher)
                    .pick(1)
                    .anything()
                    .fromAnywhere()
                    .and { fileUri -> imgAttachment.setImageURI(fileUri) }
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