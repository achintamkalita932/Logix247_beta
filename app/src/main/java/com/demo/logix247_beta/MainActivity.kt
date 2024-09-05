package com.demo.logix247_beta

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.demo.logix247_beta.activities.Dashboard
import com.demo.logix247_beta.activities.Login_activity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        // Delay to show splash screen (consider using a proper splash screen mechanism)
        Thread.sleep(1500)
        installSplashScreen()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        // Check if a user is already logged in
        if (firebaseAuth.currentUser != null) {
            // Navigate to dashboard if user is logged in
            val intent = Intent(this@MainActivity, Dashboard::class.java)
            startActivity(intent)
            // Finish MainActivity so user can't return to it
            finish()
        }else{
            // Navigate to phone authentication if user is not logged in
            startActivity(Intent(this@MainActivity, Login_activity::class.java))
            finish()
        }
    }
}