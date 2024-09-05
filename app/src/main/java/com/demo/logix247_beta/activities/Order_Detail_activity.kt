package com.demo.logix247_beta.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.demo.logix247_beta.Models.Orders
import com.demo.logix247_beta.R
import com.google.firebase.firestore.FirebaseFirestore

class Order_Detail_activity : AppCompatActivity() {

    private lateinit var orderID: TextView
    private lateinit var status: TextView
    private lateinit var date: TextView
    private lateinit var createdBy: TextView
    private lateinit var assignedTo: TextView
    private lateinit var orderDesc: TextView
    private lateinit var print: Button

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_order_detail)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        init()
        db = FirebaseFirestore.getInstance()

        val orderID = intent.getStringExtra("orderID") ?: ""
        if(orderID != null){
            fetchOrderData(orderID)
        }

        print.setOnClickListener {
            val intent = Intent(this, OrderInvoice::class.java)
            intent.putExtra("orderID", orderID)
            startActivity(intent)
        }
    }

    private fun fetchOrderData(orderID: String) {
        db.collection("Orders")
            .whereEqualTo("id", orderID)  // Query to find the order by its ID
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val order = documents.documents[0].toObject(Orders::class.java)
                    updateUI(order)
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                    Log.d("OrderDetailActivity", "No such order")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting order details", Toast.LENGTH_SHORT).show()
                Log.d("OrderDetailActivity", "Error getting order details: ", exception)
            }
    }

    private fun updateUI(order: Orders?) {
        if (order != null) {
            orderID.text = order.id
            status.text = order.orderStatus
            date.text = order.shippedDate
            createdBy.text = order.createdBy
            assignedTo.text = order.assignedTo
            orderDesc.text = order.itemDescription
            updateOrderStatus(order.orderStatus)
        } else {
            Toast.makeText(this, "Order data is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun init() {
        orderID = findViewById(R.id.order_ID)
        status = findViewById(R.id.status)
        date = findViewById(R.id.order_date)
        createdBy = findViewById(R.id.order_createdBy)
        assignedTo = findViewById(R.id.order_assignedTo)
        orderDesc = findViewById(R.id.orderDesc)
        print = findViewById(R.id.downloadInvoice)
    }

    private fun updateOrderStatus(status: String) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarOrderStatus)
        when (status) {
            "pending" -> progressBar.progress = 0
            "in Transit" -> progressBar.progress = 50
            "delivered" -> progressBar.progress = 100
        }
    }

}