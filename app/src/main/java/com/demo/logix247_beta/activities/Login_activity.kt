package com.demo.logix247_beta.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.R
import com.google.firebase.auth.FirebaseAuth

class Login_activity : AppCompatActivity() {

    // Firebase authentication instance
    private lateinit var firebaseAuth: FirebaseAuth

    // UI elements for login
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: TextView
    private lateinit var forgotPasswordTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()

        // Set up login button click listener
        loginButton.setOnClickListener {
            val email = loginEmail.text.toString().trim { it <= ' ' }
            val password = loginPassword.text.toString().trim { it <= ' ' }

            // Check if fields are not empty
            if (checking()) {
                // Sign in with email and password using Firebase Auth
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@Login_activity, "Login Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@Login_activity, Dashboard::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@Login_activity, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this@Login_activity, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up signup button click listener
        signupButton.setOnClickListener {
            val intent = Intent(this@Login_activity, SignUp_activity::class.java)
            startActivity(intent)
            finish()
        }

        forgotPasswordTV.setOnClickListener {
            val intent = Intent(this@Login_activity, ForgotPassword_activity::class.java)
            startActivity(intent)
        }
    }

    private fun checking(): Boolean {
        if (loginEmail.text.toString().trim { it <= ' ' }
                .isNotEmpty() && loginPassword.text.toString().trim { it <= ' ' }.isNotEmpty()) {
            return true
        }
        return false
    }

    private fun init(){
        firebaseAuth = FirebaseAuth.getInstance()
        loginEmail = findViewById(R.id.loginEmail)
        loginPassword = findViewById(R.id.loginPassword)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.SignUpRedirectText)
        forgotPasswordTV = findViewById(R.id.forgotPassword)
    }
}