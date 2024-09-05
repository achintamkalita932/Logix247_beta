package com.demo.logix247_beta.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.logix247_beta.Models.OnPartnerClickListener
import com.demo.logix247_beta.Models.Partner
import com.demo.logix247_beta.Models.PartnerDetails
import com.demo.logix247_beta.R
import com.demo.logix247_beta.adapter.PartnerAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class Local_Partner_activity : AppCompatActivity(), OnPartnerClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PartnerAdapter
    private lateinit var partners: List<PartnerDetails>
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_local_partner)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLocal)
        setupRecyclerView()
        loadPartners()

        searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    filterByArea(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterByArea(newText.trim()) // Optionally, update the filter with each text change
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        partners = emptyList()
        adapter = PartnerAdapter(partners, this)
        recyclerView.adapter = adapter
    }

    private fun loadPartners() {
        val db = FirebaseFirestore.getInstance()
        db.collection("USERS")
            .whereEqualTo("isPartner", true)
            .get()
            .addOnSuccessListener { documents ->
                partners = documents.mapNotNull {
                    it.toObject(Partner::class.java).partnerDetails
                }.filterNotNull().shuffled()

                Log.d("partners", partners.toString())

                adapter.updateData(partners)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore Error", "Error getting documents: ", exception)
            }
    }

    fun filterByArea(area: String) {
        val areaLowercase = area.lowercase(Locale.getDefault())
        val filteredList = partners.filter {
            it.serviceArea.any { serviceArea ->
                serviceArea.lowercase(Locale.getDefault()).contains(areaLowercase)
            }
        }
        adapter.updateData(filteredList)
        adapter.notifyDataSetChanged()
    }

    override fun onPartnerClick(partner: PartnerDetails) {
        saveCompanyDetailsToPrefs(partner.companyName, partner.companyEmail)
        // Navigate to the next activity
        val intent = Intent(this, OrderDescription::class.java)
        startActivity(intent)
    }

    private fun saveCompanyDetailsToPrefs(companyName: String, companyEmail: String) {
        val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("companyName", companyName)
            putString("companyEmail", companyEmail)
            apply()
        }
    }
}