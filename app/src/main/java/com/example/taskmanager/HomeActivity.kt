package com.example.taskmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.taskmanager.databinding.HomeActivityBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity: Activity() {
    private lateinit var binding: HomeActivityBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

}