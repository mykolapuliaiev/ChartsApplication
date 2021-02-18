package com.example.chartapplication.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.chartapplication.R
import com.example.chartapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationHost: NavHostFragment
    private lateinit var navigationController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
    }

    /**
     * Initialization of navigation component
     */
    private fun initNavigation() {
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        (fragment as? NavHostFragment)?.let { navHostFragment ->
            navigationHost = navHostFragment
            navigationController = navigationHost.navController
        }
    }
}