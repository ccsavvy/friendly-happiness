package com.example.taskmanager


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.taskmanager.databinding.HomeActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class HomeActivity: AppCompatActivity() {
    private lateinit var binding: HomeActivityBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        val bottomNavView = binding.bottomNavigationView
        bottomNavView.background = null
        bottomNavView.menu.getItem(2).isEnabled = false
        bottomNavView.setOnNavigationItemSelectedListener {
                menuItem ->
            when (menuItem.itemId) {
                R.id.statistics -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, StatisticsFragment()).commit()
                    true
                }

                R.id.home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, HomeFragment()).commit()
                    true
                }
                R.id.account -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, StatisticsFragment()).commit()
                    true
                }
                R.id.settings -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, SettingsFragment()).commit()
                    true
                }
                else -> false
            }
        }



    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer,fragment)
        transaction.commit()
    }

}