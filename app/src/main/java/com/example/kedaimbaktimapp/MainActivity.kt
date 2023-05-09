package com.example.kedaimbaktimapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kedaimbaktimapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration.Builder(
//            R.id.navigation_home,
//            R.id.navigation_dashboard,
//            R.id.navigation_notifications,
//            R.id.navigation_profile
//        ).build()
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.option_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_logout -> {
//                AlertDialog.Builder(this)
//                    .setTitle(getString(R.string.logout))
//                    .setMessage(getString(R.string.want_to_logout))
//                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
//                        signOut()
//                        Toast.makeText(
//                            applicationContext,
//                            getString(R.string.success_logout),
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                    .setNegativeButton(getString(R.string.no)) { _, _ -> }
//                    .show()
//            }
//            R.id.menu_about -> {
//                startActivity(Intent(this, AboutUsActivity::class.java))
//            }
//        }
//        return true
//    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}