package com.example.taskmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.taskmanager.databinding.LoginActivityBinding

import com.google.firebase.auth.FirebaseAuth

class LoginActivity : Activity() {

    private lateinit var binding: LoginActivityBinding

    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.signUpRedirect.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            // using finish() to end the activity
            finish()
        }

        binding.btnLogin.setOnClickListener {
            // View Binding
            val email = binding.emailEt.text.toString()
            val pass = binding.PassEt.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_SHORT).show()
                        val home = Intent(this, HomeActivity::class.java)
                        startActivity(home)
                        finish()
                    } else
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null)
        {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }


}
