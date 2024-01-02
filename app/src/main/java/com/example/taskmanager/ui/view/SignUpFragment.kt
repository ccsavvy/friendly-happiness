package com.example.taskmanager.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.taskmanager.HomeActivity
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentSignupBinding
import com.example.taskmanager.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentSignupBinding

    var TAG = "SignUpFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignupBinding.inflate(inflater, container, false)
        registerObservers()
        listenToChannels()
        binding.apply {
            signButton.setOnClickListener {
                val email = emailEt.text.toString()
                val password = PassEt.text.toString()
                val confirmPass = confPassEt.text.toString()

                viewModel.signUpUser(email = email, password = password, confirmPass = confirmPass)
            }

            RedirectLogin.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
        }
        return binding.root
    }

    private fun registerObservers() {
        viewModel.currentUser.observe(requireActivity()) { user ->
            user?.let {
                startActivity(Intent(requireContext(),HomeActivity::class.java))
            }
        }
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when (event) {
                    is AuthViewModel.AllEvents.Error -> {
                        binding?.apply {
                            Toast.makeText(requireContext(), event.error, Toast.LENGTH_SHORT).show()
                        }
                    }

                    is AuthViewModel.AllEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }

                    is AuthViewModel.AllEvents.ErrorCode -> {
                        if (event.code == 1)
                            binding?.apply {
                                emailEt.setError("Email should not be empty")
                                emailEt.requestFocus()
                                emailEt.error = "Email should not be empty"
                            }


                        if (event.code == 2)
                            binding?.apply {
                                PassEt.error = "Password should not be empty"
                            }

                        if (event.code == 3)
                            binding?.apply {
                                confPassEt.error = "Passwords do not match"
                            }
                    }

                    else -> {
                        Log.d(TAG, "listenToChannels: No event received so far")
                    }
                }

            }
        }
    }

}