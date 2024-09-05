package com.demo.logix247_beta.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.R

class DestinationAddressForOrder : AppCompatActivity() {

    private lateinit var addressLine1: EditText
    private lateinit var addressLine2: EditText
    private lateinit var Landmark: EditText
    private lateinit var state: EditText
    private lateinit var pincode: EditText
    private lateinit var saveDestinationAddress: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_destination_address_for_order)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()

        saveDestinationAddress.setOnClickListener {
            if(check()){
                // Create an Intent to start the new Activity
                val intent = Intent(this, OrderDescription::class.java)
                // Put the address details into the Intent
                intent.putExtra("addressLine1", addressLine1.text.toString())
                intent.putExtra("addressLine2", addressLine2.text.toString())
                intent.putExtra("landmark", Landmark.text.toString())
                intent.putExtra("state", state.text.toString())
                intent.putExtra("pincode", pincode.text.toString())

                // Start the next Activity
                startActivity(intent)

                // Optional: finish the current activity if you don't want it to remain in the activity stack
                finish()
            }else{
                Toast.makeText(this@DestinationAddressForOrder, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun check(): Boolean {
        if(addressLine1.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            addressLine2.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            Landmark.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            state.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            pincode.text.toString().trim { it <= ' ' }.isNotEmpty()){
            return true
        }
        return false
    }

    private fun init(){
        addressLine1 = findViewById(R.id.Address_Line_1_for_delivery)
        addressLine2 = findViewById(R.id.Address_Line_2_for_delivery)
        Landmark = findViewById(R.id.Landmark_for_delivery)
        state = findViewById(R.id.State_for_delivery)
        pincode = findViewById(R.id.Pincode_for_delivery)
        saveDestinationAddress = findViewById(R.id.saveDestinationAddress)
    }
}