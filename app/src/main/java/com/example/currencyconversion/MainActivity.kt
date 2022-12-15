package com.example.currencyconversion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val defaultHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController = (defaultHostFragment as NavHostFragment).navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }
}