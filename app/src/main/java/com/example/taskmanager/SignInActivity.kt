package com.example.taskmanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.taskmanager.databinding.SigninActivityBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : Activity() {
    private lateinit var binding: SigninActivityBinding

    // create Firebase authentication object
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SigninActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.RedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            // using finish() to end the activity
            finish()
        }
        // View Bindings
        binding.signButton.setOnClickListener{
            val email = binding.emailEt.text.toString()
            val pass = binding.PassEt.text.toString()
            val confPass = binding.confPassEt.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty() && confPass.isNotEmpty()){
                if(pass == confPass){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(){
                        if(it.isSuccessful){
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Log.e("firebase",it.exception.toString())
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()

                        }
                    }
                }
                else{
                    Toast.makeText(this, "Password not matching", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show()
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
