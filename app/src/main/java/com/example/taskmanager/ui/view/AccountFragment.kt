package com.example.taskmanager.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskmanager.MainActivity
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentAccountBinding
import com.example.taskmanager.viewModel.AccountViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {


    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentAccountBinding
    private val viewModel: AccountViewModel by viewModels()

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
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getUser() {
        viewModel.getCurrentUser()
    }

    private fun registerObserver() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    viewModel.currentUser.observe(viewLifecycleOwner) { user ->
        user?.let {
            binding.apply {
                email.setText(it.email)
                btnLogout.setOnClickListener {
                    viewModel.signOut()
                    startActivity(Intent(requireContext(),MainActivity::class.java))
                }
            }
        }
    }

//        btn.setOnClickListener {
//            auth.signOut()
//            val intent = Intent(activity, LoginActivity::class.java)
//            activity?.startActivity(intent)
//        }
    }

}