package com.example.taskmanager.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorisationManager @Inject constructor(@ApplicationContext context: Context){
    val firebaseAuth = FirebaseAuth.getInstance()

    val uId = firebaseAuth.currentUser?.uid ?: "MycJQECdEuUUovrRwZZspWOsDEA2"
}