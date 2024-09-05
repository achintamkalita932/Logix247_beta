package com.demo.logix247_beta.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.logix247_beta.Models.OnOrderClickListener
import com.demo.logix247_beta.Models.Orders
import com.demo.logix247_beta.R
import com.demo.logix247_beta.activities.Order_Detail_activity
import com.demo.logix247_beta.adapter.ordersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Shipments_fragment : Fragment(), OnOrderClickListener {

    private lateinit var adapter: ordersAdapter
    private lateinit var db : FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipments_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        setupRecyclerView()
        fetchOrders()
    }

    private fun fetchOrders() {
        val currentUserEmail = auth.currentUser?.email.toString()
        db.collection("Orders")
            .whereEqualTo("createdBy", currentUserEmail) // Filter orders by the 'createdBy' field
            .get()
            .addOnSuccessListener { documents ->
                val ordersList = documents.map { document ->
                    document.toObject(Orders::class.java) // Convert each document to an Orders object
                }
                adapter.updateData(ordersList) // Update the adapter's data
            }
            .addOnFailureListener { exception ->
                Log.e("MyShipmentsFragment", "Error getting documents: ", exception)
            }
    }

    private fun setupRecyclerView() {
        recyclerView = view?.findViewById(R.id.ordersRecyclerView) ?: throw IllegalStateException("View not found")
        adapter = ordersAdapter(emptyList(), this) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.adapter = null // Clear the adapter
        // If you have any other cleanup related to the view, do it here
    }

    override fun onOrderClick(order: Orders) {
        val intent = Intent(context, Order_Detail_activity::class.java)
        intent.putExtra("orderID", order.id)  // Assume each order has an ID that uniquely identifies it
        startActivity(intent)
    }
}