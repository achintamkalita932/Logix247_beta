package com.demo.logix247_beta.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.demo.logix247_beta.R
import com.demo.logix247_beta.fragments.Home_Fragment
import com.demo.logix247_beta.fragments.Profile_Fragment
import com.demo.logix247_beta.fragments.Search_Fragment
import com.demo.logix247_beta.fragments.Shipments_fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Dashboard : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        init()
        // Set up the bottom navigation item selected listener
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            displaySelectedFragment(menuItem.itemId)
            true
        }

        // Initially display the HomeFragment if no saved instance state is available
        if (savedInstanceState == null) {
            replaceFragment(Home_Fragment(), "homeFragmentTag", false)
            bottomNavigationView.selectedItemId = R.id.home
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment, tag)
        if (addToBackStack) {
            transaction.addToBackStack(tag)
        }
        transaction.commit()
    }

    fun displaySelectedFragment(itemId: Int) {
        when (itemId) {
            R.id.home -> {
                replaceFragment(Home_Fragment(), "homeFragmentTag", false)
            }

            R.id.search -> {
                replaceFragment(Search_Fragment(), "searchFragmentTag", true)
            }

            R.id.shipments -> {
                replaceFragment(Shipments_fragment(), "shipmentsFragmentTag", true)
            }

            R.id.profile -> {
                replaceFragment(Profile_Fragment(), "profileFragmentTag", true)
            }
        }
    }

    private fun init(){
        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    // Handle back press
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frame_container)

        if (currentFragment is Home_Fragment) {
            // Show exit dialog if in HomeFragment
            currentFragment.showExitConfirmationDialog()
        } // Check if the current fragment is not HomeFragment
        else if (currentFragment is Shipments_fragment || currentFragment is Search_Fragment ||
            currentFragment is Profile_Fragment
        ) {
            // If the current fragment is MyShipmentsFragment, navigate to HomeFragment
            replaceFragment(Home_Fragment(), "homeFragmentTag", false)
            bottomNavigationView.selectedItemId = R.id.home
        } else if (fragmentManager.backStackEntryCount > 0) {
            // Pop the back stack if there are fragments in it
            fragmentManager.popBackStack()
            // Update the bottom navigation view to match the new top fragment
            updateBottomNavigation(fragmentManager)
        } else {
            // Otherwise, let the super class handle the back press
            super.onBackPressed()
        }
    }

    private fun updateBottomNavigation(fragmentManager: FragmentManager) {
        val newTopFragment = fragmentManager.findFragmentById(R.id.frame_container)
        // Update the bottom navigation view
        when (newTopFragment) {
            is Home_Fragment -> bottomNavigationView.selectedItemId = R.id.home
            is Search_Fragment -> bottomNavigationView.selectedItemId = R.id.search
            is Shipments_fragment -> bottomNavigationView.selectedItemId = R.id.shipments
            is Profile_Fragment -> bottomNavigationView.selectedItemId = R.id.profile
        }
    }
}