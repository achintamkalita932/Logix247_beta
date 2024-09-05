package com.demo.logix247_beta.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.demo.logix247_beta.Models.AdsI
import com.demo.logix247_beta.Models.OnOrderClickListener
import com.demo.logix247_beta.Models.Orders
import com.demo.logix247_beta.R
import com.demo.logix247_beta.activities.Dashboard
import com.demo.logix247_beta.activities.Local_Partner_activity
import com.demo.logix247_beta.activities.Order_Detail_activity
import com.demo.logix247_beta.activities.Save_Address_activity
import com.demo.logix247_beta.adapter.AdsAdapter_I
import com.demo.logix247_beta.adapter.ordersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Home_Fragment : Fragment(), OnOrderClickListener {

    private lateinit var personName: TextView
    private lateinit var myshipments: LinearLayout
    private lateinit var LocalPartners: LinearLayout
    private lateinit var packersandmovers: LinearLayout
    private lateinit var calculator: LinearLayout
    private lateinit var support: LinearLayout
    private lateinit var digitalVault: LinearLayout
    private lateinit var moreShipments: TextView
    private lateinit var defaultAddressTV : TextView
    private lateinit var notification: ImageView

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adsRecyclerView: RecyclerView
    private lateinit var adsList: ArrayList<AdsI>
    private lateinit var adsAdapter: AdsAdapter_I
    private lateinit var orderAdapter: ordersAdapter
    private lateinit var db: FirebaseFirestore

    private lateinit var autoScrollHandler: Handler
    private lateinit var autoScrollRunnable: Runnable
    private var currentPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        setupRecyclerView()
        fetchOrders()


        val email = firebaseAuth.currentUser?.email
        if (email != null) {
            fetchUsername(email)
            fetchDefaultAddress(email)
        }

        myshipments.setOnClickListener {
            Toast.makeText(requireContext(), "My Shipments", Toast.LENGTH_SHORT).show()
            (activity as? Dashboard)?.displaySelectedFragment(R.id.shipments)
        }

        LocalPartners.setOnClickListener {
            Toast.makeText(requireContext(), "Local Partners", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, Local_Partner_activity::class.java))
        }

        packersandmovers.setOnClickListener {
            Toast.makeText(requireContext(), "Comming Soon", Toast.LENGTH_SHORT).show()
        }

        calculator.setOnClickListener {
            Toast.makeText(requireContext(), "Comming Soon", Toast.LENGTH_SHORT).show()
        }

        support.setOnClickListener {
            Toast.makeText(requireContext(), "Comming Soon", Toast.LENGTH_SHORT).show()
        }

        moreShipments.setOnClickListener {
            Toast.makeText(requireContext(), "My Shipments", Toast.LENGTH_SHORT).show()
            (activity as? Dashboard)?.displaySelectedFragment(R.id.shipments)
        }

        defaultAddressTV.setOnClickListener {
            Toast.makeText(requireContext(), "Save Address", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, Save_Address_activity::class.java))
        }

        notification.setOnClickListener {
            Toast.makeText(requireContext(), "Comming Soon", Toast.LENGTH_SHORT).show()
        }

        digitalVault.setOnClickListener {
            Toast.makeText(requireContext(), "Comming Soon", Toast.LENGTH_SHORT).show()
        }

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
                        val pincode = address["pincode"] as? String ?: ""

                        val fullAddress = "$addressLine1, $pincode ..."
                        defaultAddressTV.text = fullAddress
                    } ?: run {
//                        Toast.makeText(context, "No default address found.", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
//                    Toast.makeText(context, "No addresses found.", Toast.LENGTH_SHORT).show()
                }
            } else {
//                Toast.makeText(context, "User document not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
//            Toast.makeText(context, "Error fetching addresses: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUsername(email: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("USERS").document(email)

        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val name = document.getString("Name") ?: "Unknown"
                // Now you have the user's name, you can set it to the TextView
                personName.text = name
            } else {
                Log.d("Firestore", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "get failed with ", exception)
        }
    }

    private fun fetchOrders() {
        if (!isNetworkAvailable(requireContext())) {
            showNoInternetDialog()
        } else {
            val currentUserEmail = firebaseAuth.currentUser?.email.toString()
            db.collection("Orders")
                .whereEqualTo("createdBy", currentUserEmail)
                .limit(10) // Filter orders by the 'createdBy' field
                .get()
                .addOnSuccessListener { documents ->
                    val ordersList = documents.map { document ->
                        document.toObject(Orders::class.java) // Convert each document to an Orders object
                    }
                    orderAdapter.updateData(ordersList) // Update the adapter's data
                }
                .addOnFailureListener { exception ->
                    Log.e("MyShipmentsFragment", "Error getting documents: ", exception)
                }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = view?.findViewById(R.id.ordersRecyclerView1)
            ?: throw IllegalStateException("View not found")
        orderAdapter = ordersAdapter(emptyList(), this) // Initialize the adapter with an empty list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = orderAdapter
    }

    private fun init() {
        personName = requireView().findViewById(R.id.personName)
        myshipments = requireView().findViewById(R.id.MyShipments)
        LocalPartners = requireView().findViewById(R.id.localPartners)
        packersandmovers = requireView().findViewById(R.id.packersMovers)
        calculator = requireView().findViewById(R.id.Calculator)
        support = requireView().findViewById(R.id.customerService)
        digitalVault = requireView().findViewById(R.id.digital_vault)
        moreShipments = requireView().findViewById(R.id.moreShipments)
        defaultAddressTV = requireView().findViewById(R.id.defaultAddress)
        notification = requireView().findViewById(R.id.notificationIcon)

        adsRecyclerView = requireView().findViewById(R.id.advertisingRecyclerView1)
        adsRecyclerView.setHasFixedSize(true)
        adsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(adsRecyclerView)

        adsList = ArrayList()

        addDataToList()
        adsAdapter = AdsAdapter_I(adsList)
        adsRecyclerView.adapter = adsAdapter

        autoScrollAds()
    }

    private fun addDataToList() {
        adsList.add(AdsI(R.drawable.ads1new))
        adsList.add(AdsI(R.drawable.ads2new))
        adsList.add(AdsI(R.drawable.ads4))
        adsList.add(AdsI(R.drawable.logix_ads_new))
        adsList.add(AdsI(R.drawable.ads5_new))
    }

    private fun autoScrollAds() {
        autoScrollHandler = Handler(Looper.getMainLooper())
        autoScrollRunnable = object : Runnable {
            override fun run() {
                if (adsAdapter.itemCount == 0) return

                if (currentPosition == adsAdapter.itemCount) {
                    currentPosition = 0
                }
                adsRecyclerView.smoothScrollToPosition(currentPosition)
                currentPosition++
                autoScrollHandler.postDelayed(this, 3000) // Change 3000 to the interval you want (in milliseconds)
            }
        }
        autoScrollHandler.post(autoScrollRunnable)
    }

    override fun onOrderClick(order: Orders) {
        Toast.makeText(requireContext(), "Clicked on order: ${order.id}", Toast.LENGTH_SHORT).show()
        // Optionally, start a new activity or fragment to display detailed order information
        val intent = Intent(context, Order_Detail_activity::class.java)
        intent.putExtra("orderID", order.id)  // Assume each order has an ID that uniquely identifies it
        startActivity(intent)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || it.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("No Internet Connection")
            setMessage("Please check your internet connection and try again.")
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Exit App")
        builder.setMessage("Are you sure you want to exit?")
        builder.setPositiveButton("Yes") { dialog, which ->
            activity?.finish()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }
}