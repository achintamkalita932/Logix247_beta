package com.demo.logix247_beta.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.R
import com.demo.logix247_beta.adapter.AddressAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Save_Address_activity : AppCompatActivity() {

    private lateinit var addressLine1: EditText
    private lateinit var addressLine2: EditText
    private lateinit var Landmark: EditText
    private lateinit var state: EditText
    private lateinit var pincode: EditText
    private lateinit var save: Button
    private lateinit var address: Map<String, String>
    private lateinit var listView: ListView

    private lateinit var email: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_save_address)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()
        firebaseAuth = FirebaseAuth.getInstance()
        email = firebaseAuth.currentUser?.email.toString()
        database = FirebaseFirestore.getInstance()
        loadUserAddresses()

        save.setOnClickListener {
            // Get user input
            address = mapOf(
                "addressLine1" to addressLine1.text.toString(),
                "addressLine2" to addressLine2.text.toString(),
                "landmark" to Landmark.text.toString(),
                "state" to state.text.toString(),
                "pincode" to pincode.text.toString()
            )

            // Check if all fields are filled
            if (checkinput()) {
                if (pincode.text.toString().trim { it <= ' ' }.length == 6) {
                    val userRef = database.collection("USERS").document(email)
                    // Get the user document
                    userRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userAddresses = document.get("addresses") as? ArrayList<Map<String, String>> ?: arrayListOf()
                            userAddresses.add(address)

                            // Update the document with the new address
                            userRef.update("addresses", userAddresses)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Address added successfully")
                                    Toast.makeText(this, "Address added successfully", Toast.LENGTH_SHORT).show()
                                    loadUserAddresses()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error adding address", e)
                                    Toast.makeText(this, "Error adding address", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }


                } else {
                    Toast.makeText(this, "Please enter a valid pincode", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }

            loadUserAddresses()
        }
    }

    private fun checkinput(): Boolean {
        if (addressLine1.text.toString().trim { it <= ' ' }.isEmpty() &&
            addressLine2.text.toString().trim { it <= ' ' }.isEmpty() &&
            Landmark.text.toString().trim { it <= ' ' }.isEmpty() &&
            state.text.toString().trim { it <= ' ' }.isEmpty() &&
            pincode.text.toString().trim { it <= ' ' }.isEmpty()
        ) {
            return false
        } else return true
    }

    private fun init() {
        addressLine1 = findViewById(R.id.Address_Line_1)
        addressLine2 = findViewById(R.id.Address_Line_2)
        Landmark = findViewById(R.id.Landmark)
        state = findViewById(R.id.State)
        pincode = findViewById(R.id.Pincode)
        save = findViewById(R.id.saveUserAddress)
        listView = findViewById(R.id.addressListView)
    }

    private fun loadUserAddresses() {
        val userRef = database.collection("USERS").document(email)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val addresses = document.get("addresses") as? MutableList<MutableMap<String, Any>> ?: mutableListOf()
                val adapter = AddressAdapter(this, addresses, email, database)
                listView.adapter = adapter
            }
        }
    }
}