package com.kruelkotlinkiller.krowd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutActivty : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_activity)
        val nav = findViewById<BottomNavigationView>(R.id.nav5)
        nav.setOnNavigationItemReselectedListener { item->
            when(item.itemId){
                R.id.backHome->{
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                }
            }
        }
    }
}
