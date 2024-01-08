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
import com.example.taskmanager.MainActivity
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAccountBinding
import com.example.taskmanager.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var binding: FragmentAccountBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater,container,false)
        getUser()
        registerObserver()
        listenToChannels()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getUser() {
        viewModel.getCurrentUser()
    }

    private fun registerObserver() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.apply {
                    email.setText(it.email)
                    btnLogout.setOnClickListener {
                        viewModel.signOut()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


                    else -> {
                        Log.d("Acccount fragment", "listenToChannels: No event received so far")
                    }
                }

            }
        }
    }

}