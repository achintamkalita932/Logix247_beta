package com.demo.logix247_beta.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore

class SignUp_activity : AppCompatActivity() {

    // UI elements for user details
    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var userPassword: EditText
    private lateinit var continueButton: Button
    private lateinit var loginButton: TextView

    // Firebase authentication and Firestore instances
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()

        // Set up continue button click listener
        continueButton.setOnClickListener {
            if (checking()) {
                if (userPassword.length() < 10 || phoneNumber.length() < 10) {
                    Toast.makeText(this@SignUp_activity, "Password and Phone Number must be at least 10 characters long", Toast.LENGTH_SHORT).show()
                } else {
                    val database_name = userName.text.toString().trim { it <= ' ' }
                    val database_email = userEmail.text.toString().trim { it <= ' ' }
                    val database_phone = phoneNumber.text.toString().trim { it <= ' ' }
                    val user = hashMapOf(
                        "Name" to database_name,
                        "Email" to database_email,
                        "Phone" to database_phone
                    )

                    val Users = database.collection("USERS")
                    val query = Users.whereEqualTo("Email", database_email).get()
                        .addOnSuccessListener { task ->
                            if (task.isEmpty) {
                                firebaseAuth.createUserWithEmailAndPassword(database_email, userPassword.text.toString())
                                    .addOnSuccessListener { task ->
                                    if (task.user != null) {
                                        Users.document(database_email).set(user)
                                        Toast.makeText(this@SignUp_activity, "User Account is Created", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@SignUp_activity, OtpVerification_activity::class.java)
                                        intent.putExtra("phone", "+91${database_phone}")
                                        Log.d("TAG", "+91${database_phone}")
                                        startActivity(intent)
                                        finish()
                                    }
                                }.addOnFailureListener { e ->
                                    Toast.makeText(this@SignUp_activity, "Error !" + e.toString(), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@SignUp_activity, "User already exists", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, Login_activity::class.java))
                                finish()
                            }
                        }
                }
            } else {
                Toast.makeText(this@SignUp_activity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up login button click listener
        loginButton.setOnClickListener {
            startActivity(Intent(this@SignUp_activity, Login_activity::class.java))
            finish()
        }
    }

    private fun checking(): Boolean {
        if (userName.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            userEmail.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            phoneNumber.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            userPassword.text.toString().trim { it <= ' ' }.isNotEmpty()
        ) {
            return true
        }
        return false
    }

    private fun init(){
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        userName = findViewById(R.id.signupusername)
        userEmail = findViewById(R.id.signupEmail)
        phoneNumber = findViewById(R.id.signupPhone)
        userPassword = findViewById(R.id.signupPassword)
        continueButton = findViewById(R.id.continueButton)
        loginButton = findViewById(R.id.loginRedirectText)
    }
}