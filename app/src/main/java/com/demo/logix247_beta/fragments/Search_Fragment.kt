package com.demo.logix247_beta.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.demo.logix247_beta.R
import com.demo.logix247_beta.activities.Local_Partner_activity

class Search_Fragment : Fragment() {

    private lateinit var courier: LinearLayout
    private lateinit var localPartners: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the search functionality here
        init()

        courier.setOnClickListener {
            Toast.makeText(requireContext(), "Comming Soon", Toast.LENGTH_SHORT).show()
        }

        localPartners.setOnClickListener {
            Toast.makeText(requireContext(), "Local Partners", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, Local_Partner_activity::class.java))
        }
    }

    private fun init() {
        courier = requireView().findViewById(R.id.logisticCourier)
        localPartners = requireView().findViewById(R.id.localPartnersSearch)
    }
}