package com.demo.logix247_beta.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.demo.logix247_beta.R
import com.demo.logix247_beta.activities.Login_activity
import com.demo.logix247_beta.activities.Save_Address_activity
import com.google.firebase.auth.FirebaseAuth

class Profile_Fragment : Fragment() {

    private lateinit var profile: TextView
    private lateinit var saveAddress: TextView
    private lateinit var deleteAccount: TextView
    private lateinit var aboutUs: TextView
    private lateinit var logoutButton: Button
    private lateinit var rateUs: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        firebaseAuth = FirebaseAuth.getInstance()

        profile.setOnClickListener{
            Toast.makeText(activity, "Profile, Comming Soon", Toast.LENGTH_LONG).show()
        }

        aboutUs.setOnClickListener{
            val url = "https://sites.google.com/view/logix247/home"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        saveAddress.setOnClickListener {
            Toast.makeText(activity, "Save Address", Toast.LENGTH_LONG).show()
            startActivity(Intent(activity, Save_Address_activity::class.java))
        }

        deleteAccount.setOnClickListener {
            val url = "https://forms.gle/6qsYeaauyheL7zsM9"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        rateUs.setOnClickListener {
            Toast.makeText(activity, "Comming Soon", Toast.LENGTH_LONG).show()
        }

        logoutButton.setOnClickListener{
            firebaseAuth.signOut()
            Toast.makeText(activity, "Logged out Successfully", Toast.LENGTH_LONG).show()
            startActivity(Intent(activity, Login_activity::class.java))
            activity?.finish()
        }
    }

    private fun init(){
        profile = requireView().findViewById(R.id.user_Profile)
        saveAddress = requireView().findViewById(R.id.save_Address)
        deleteAccount = requireView().findViewById(R.id.deleteYourProfile)
        logoutButton = requireView().findViewById(R.id.logoutButton)
        aboutUs = requireView().findViewById(R.id.aboutUS)
        rateUs = requireView().findViewById(R.id.Rate_Us)
    }
}