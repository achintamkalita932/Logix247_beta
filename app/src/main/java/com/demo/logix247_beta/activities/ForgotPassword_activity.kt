package com.demo.logix247_beta.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword_activity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var proceed: Button
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()

        proceed.setOnClickListener {
            val email = email.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this@ForgotPassword_activity, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this@ForgotPassword_activity, "Password reset email sent", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this@ForgotPassword_activity, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(){
        firebaseAuth = FirebaseAuth.getInstance()
        email = findViewById(R.id.ResetPasswordEmail)
        proceed = findViewById(R.id.Proceed)
    }
}