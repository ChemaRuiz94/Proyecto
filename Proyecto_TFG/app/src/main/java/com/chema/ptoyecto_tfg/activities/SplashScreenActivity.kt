package com.chema.ptoyecto_tfg.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chema.ptoyecto_tfg.R
import com.chema.ptoyecto_tfg.MainActivity

import android.content.Intent
import android.os.Handler
import android.os.Looper


class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ocultamos la barra de action
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen)

        val intent = Intent(
            applicationContext,
            LoginActivity::class.java)


        Handler(Looper.getMainLooper()).postDelayed({
            run {
                startActivity(intent)
                finish()
            }
        }, 3000)



    }
}