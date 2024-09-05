package com.demo.logix247_beta.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.Models.Orders
import com.demo.logix247_beta.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID.randomUUID

class OrderDescription : AppCompatActivity() {

    // initialize the destination Address
    private lateinit var addressline1ForDestination: String
    private lateinit var addressline2ForDestination: String
    private lateinit var landmarkForDestination: String
    private lateinit var stateForDestination: String
    private lateinit var pincodeForDestination: String

    // Delivery partner details
    private lateinit var companyName: String
    private lateinit var companyEmail: String

    // User details
    private lateinit var createdByEmail: String
    private lateinit var personName: String

    // address for delivery
    private lateinit var destination: String

    // address for pickup
    private lateinit var pickUpAddress: String
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var pickUpLocation: TextView
    private lateinit var dropLocation: LinearLayout
    private lateinit var paymentMode: RadioButton
    private lateinit var orderProceed: Button
    private lateinit var itemDescription: EditText
    private lateinit var quantity: EditText
    private lateinit var weightAndSize: EditText
    private lateinit var valueGoods: EditText
    private lateinit var packagingType: EditText
    private lateinit var fragileGoods: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_order_description)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        createdByEmail = auth.currentUser?.email.toString()
        db = FirebaseFirestore.getInstance()

        loadCompanyDetailsFromPrefs()

        // Get the destination address from the intent
        addressline1ForDestination = intent.getStringExtra("addressLine1").toString()
        addressline2ForDestination = intent.getStringExtra("addressLine2").toString()
        landmarkForDestination = intent.getStringExtra("landmark").toString()
        stateForDestination = intent.getStringExtra("state").toString()
        pincodeForDestination = intent.getStringExtra("pincode").toString()
        destination =
            addressline1ForDestination + ", " + addressline2ForDestination + ", " +
                    landmarkForDestination + ", " + stateForDestination + ", " + pincodeForDestination

        fetchUsername(createdByEmail)
        fetchDefaultAddress(createdByEmail)

        pickUpLocation.setOnClickListener { it ->
            startActivity(Intent(this,Save_Address_activity::class.java))
        }

        dropLocation.setOnClickListener {
            startActivity(Intent(this, DestinationAddressForOrder::class.java))
        }

        orderProceed.setOnClickListener {
            if (validate()) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val currentDateTime = dateFormat.format(Date()) // Get current time
                val orderID = randomUUID().toString()

                val order = Orders(
                    id = orderID.replace("-", "").substring(0, 10),
                    createdBy = createdByEmail,
                    assignedTo = companyEmail,
                    itemOwner = personName,
                    deliveryPartner = companyName,
                    orderStatus = "pending",
                    itemDescription = itemDescription.text.toString().trim { it <= ' ' },
                    quantity = quantity.text.toString().trim { it <= ' ' },
                    weightAndSize = weightAndSize.text.toString().trim { it <= ' ' },
                    valueGoods = valueGoods.text.toString().trim { it <= ' ' },
                    packagingType = packagingType.text.toString().trim { it <= ' ' },
                    fragileGoods = fragileGoods.text.toString().trim { it <= ' ' },
                    paymentModeForDelivery = if (paymentMode.isChecked) "Payment at Loading" else "Payment at Destination",
                    shippingAddressForDelivery = destination,
                    pickupAddressForDelivery = pickUpAddress,
                    shippedDate = currentDateTime
                )
                saveOrderToFirestore(order)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCompanyDetailsFromPrefs() {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        companyName =
            sharedPreferences.getString("companyName", "Default Company") ?: "Default Company"
        companyEmail = sharedPreferences.getString("companyEmail", "default@example.com")
            ?: "default@example.com"
    }


    private fun validate(): Boolean {
        if (itemDescription.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            quantity.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            weightAndSize.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            valueGoods.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            packagingType.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            fragileGoods.text.toString().trim { it <= ' ' }.isNotEmpty() &&
            paymentMode.isChecked &&
            destination.isNotEmpty()
        ) return true
        else return false
    }

    private fun saveOrderToFirestore(order: Orders) {
        db.collection("Orders").add(order)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    this,
                    "Order submitted with ID: ${documentReference.id}",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this, OrderInvoice::class.java)
                intent.putExtra("orderID", order.id)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error submitting order: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun init() {
        pickUpLocation = findViewById(R.id.pickUpAddress)
        dropLocation = findViewById(R.id.dropLocation)
        paymentMode = findViewById(R.id.paymentAtLoading)
        orderProceed = findViewById(R.id.proceedButton)
        itemDescription = findViewById(R.id.itemDescription)
        quantity = findViewById(R.id.quantityOfItem)
        weightAndSize = findViewById(R.id.weightAndSize)
        valueGoods = findViewById(R.id.valueofGoods)
        packagingType = findViewById(R.id.packagingType)
        fragileGoods = findViewById(R.id.fragileGoods)
    }

    private fun fetchDefaultAddress(email: String) {
        val docRef = db.collection("USERS").document(email)

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val addresses = document.get("addresses") as? List<Map<String, Any>>
                addresses?.let {
                    val defaultAddress = it.find { address -> address["isDefault"] == true }
                    defaultAddress?.let { address ->
                        val addressLine1 = address["addressLine1"] as? String ?: ""
                        val addressLine2 = address["addressLine2"] as? String ?: ""
                        val landmark = address["landmark"] as? String ?: ""
                        val state = address["state"] as? String ?: ""
                        val pincode = address["pincode"] as? String ?: ""


                        val fullAddress =
                            "$addressLine1, $addressLine2, $landmark, $state, $pincode"
                        pickUpAddress = fullAddress
                        pickUpLocation.text = "$addressLine1, $pincode"
                    } ?: run {
                        Toast.makeText(this, "No default address found.", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this, "No addresses found.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User document not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this,
                "Error fetching addresses: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun fetchUsername(email: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("USERS").document(email)

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val name = document.getString("Name") ?: "Unknown"
                // Now you have the user's name, you can set it to the TextView
                personName = name
            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "get failed with ", exception)
        }
    }

    private fun saveCompanyDetailsToPrefs() {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("companyName", companyName)
            putString("companyEmail", companyEmail)
            apply()
        }
    }

    override fun onPause() {
        super.onPause()
        saveCompanyDetailsToPrefs()
    }
}