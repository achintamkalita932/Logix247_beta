package com.demo.logix247_beta.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider

class OtpVerification_activity : AppCompatActivity() {

    // UI elements for OTP verification
    private lateinit var otpEtv: EditText
    private lateinit var verifyButton: Button
    private lateinit var resendOtpTv: TextView

    // Firebase authentication and phone verification variables
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var phoneNumber: String
    private lateinit var verificationId: String

    // private lateinit var phoneAuthCredential: PhoneAuthCredential
    private lateinit var token: PhoneAuthProvider.ForceResendingToken
    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_otp_verification)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        // Get phone number from the intent
        val data = getIntent().getStringExtra("phone")
        if (data != null) {
            phoneNumber = data.toString()
        }

        init()
        // Callbacks for phone authentication
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verifyAuthenticaion(credential)
                resendOtpTv.visibility = View.GONE
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@OtpVerification_activity, "OTP verification Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                super.onCodeAutoRetrievalTimeOut(verificationId)
                resendOtpTv.visibility = View.VISIBLE
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                this@OtpVerification_activity.verificationId = verificationId
                this@OtpVerification_activity.token = token
                resendOtpTv.visibility = View.GONE
            }
        }

        // Send OTP to the phone number
        sendOtp(phoneNumber)

        // Set up verify button click listener
        verifyButton.setOnClickListener {
            if (checkOtp()) {
                if (otpEtv.text.toString().length == 6) {
                    val credential = PhoneAuthProvider.getCredential(verificationId, otpEtv.text.toString())
                    verifyAuthenticaion(credential)
                } else {
                    Toast.makeText(this@OtpVerification_activity, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@OtpVerification_activity, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up resend OTP text view click listener
        resendOtpTv.setOnClickListener {
            resendOtp(phoneNumber)
        }
    }

    private fun checkOtp(): Boolean {
        if (otpEtv.text.toString().trim { it <= ' ' }.isNotEmpty()) {
            return true
        }
        return false
    }

    private fun sendOtp(phone: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)       // Phone number to verify
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)           // Activity (for callback binding)
            .setCallbacks(mCallbacks)    // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendOtp(phone: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)       // Phone number to verify
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)           // Activity (for callback binding)
            .setCallbacks(mCallbacks)    // OnVerificationStateChangedCallbacks
            .setForceResendingToken(token) // Token for resending OTP
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyAuthenticaion(phoneAuthCredential: PhoneAuthCredential) {
        firebaseAuth.currentUser?.linkWithCredential(phoneAuthCredential)?.addOnSuccessListener {
            Toast.makeText(this@OtpVerification_activity, "Account Created and Linked.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@OtpVerification_activity, Dashboard::class.java))
            finish()
        }?.addOnFailureListener {
            Toast.makeText(this@OtpVerification_activity, "Failed to link account: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun init(){
        firebaseAuth = FirebaseAuth.getInstance()
        otpEtv = findViewById(R.id.otpVerifyEtv)
        verifyButton = findViewById(R.id.verifyOtpBtn)
        resendOtpTv = findViewById(R.id.resendOtpTv)
    }
}