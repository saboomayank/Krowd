package com.kruelkotlinkiller.krowd

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.kruelkotlinkiller.krowd.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private var MY_PERMISSIONS_REQUEST_READ_LOCATION = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_READ_LOCATION)

        } else {

            if (savedInstanceState == null) {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.container, MainPage.newInstance())
//                    .commitNow()
                drawerLayout = binding.drawerLayout
                val navController = this.findNavController(R.id.myNavHostFragment)

                //for the up button
                NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
                //to now about the nav view
                NavigationUI.setupWithNavController(binding.navView, navController)
            }
        }



    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }
}





