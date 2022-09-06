package com.example.smartestmovieapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.smartestmovieapp.databinding.ActivityMainBinding
import com.example.smartestmovieapp.presentation.screens.authentication.AuthenticationActivity
import com.example.smartestmovieapp.presentation.screens.home.HomeScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.concurrent.schedule

const val SPLASH_SCREEN_DURATION = 1500L

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        firebaseAuth = Firebase.auth
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        goToNextScreen()
    }

    private fun userLogged() = firebaseAuth.currentUser != null

    private fun goToNextScreen() {
        if (userLogged()) {
            Timer().schedule(SPLASH_SCREEN_DURATION) {
                val intent = Intent(this@MainActivity, HomeScreenActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Timer().schedule(SPLASH_SCREEN_DURATION) {
                val intent = Intent(this@MainActivity, AuthenticationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

}