package com.liztstudio.runtime.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.liztstudio.runtime.R
import com.liztstudio.runtime.databinding.ActivityMainBinding
import com.liztstudio.runtime.utils.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        navigateToTrackingFragment(intent)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.runFragment, R.id.statisticFragment, R.id.settingFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setOnNavigationItemReselectedListener {  }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingFragment, R.id.runFragment, R.id.statisticFragment ->
                    binding.navView.visibility = View.VISIBLE

                else -> binding.navView.visibility = View.GONE
            }

        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragment(intent)
    }

    private fun navigateToTrackingFragment(intent: Intent?) {
        if (intent?.action == Constant.ACTION_SHOW_TRACKING_FRAGMENT) {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)

            navController.navigate(R.id.action_global_trackingFragment)
        }
    }


}