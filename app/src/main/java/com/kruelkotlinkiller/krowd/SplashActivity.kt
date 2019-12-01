package com.kruelkotlinkiller.krowd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager

class SplashActivity : AppCompatActivity() {
//    private lateinit var binding : SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)

        //4second splash time
        Handler().postDelayed({
            //start main activity
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            //finish this activity
            finish()
        },4000)


    }
    }

