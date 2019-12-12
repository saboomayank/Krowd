package com.kruelkotlinkiller.krowd

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kruelkotlinkiller.krowd.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Build
import android.util.Log


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    lateinit var email : TextView
    private var MY_PERMISSIONS_REQUEST_READ_LOCATION = 1
    lateinit var tool : androidx.appcompat.widget.Toolbar
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

//                //for the up button
//                NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
                //to now about the nav view
                tool = binding.tool
                setSupportActionBar(tool)
                NavigationUI.setupWithNavController(binding.navView, navController)
            }
        }
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.setHomeButtonEnabled(false)
        val navigationView = binding.navView
        val drawer = binding.drawerLayout
        val hView = navigationView.getHeaderView(0)
        val text = hView.findViewById<TextView>(R.id.textView20)

            text.text = "Welcome User"

        navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.about ->{
                    val i = Intent(this, AboutActivty::class.java)
                    drawer.closeDrawers()
                    startActivity(i)
                }
            }
            false
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 -> {
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("Hey", "I am nottt empty")
                }
                else{
                    this.finishAffinity()
                }
            }
        }
    }

}





