package com.demo.logix247_beta.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.demo.logix247_beta.databinding.ItemAddressBinding
import kotlin.collections.MutableList
import kotlin.collections.MutableMap

class AddressAdapter(
    context: Context,
    private val addresses: MutableList<MutableMap<String, Any>>,
    private val email: String,
    private val database: FirebaseFirestore
) : ArrayAdapter<MutableMap<String, Any>>(context, 0, addresses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemAddressBinding

        if (convertView == null) {
            binding = ItemAddressBinding.inflate(LayoutInflater.from(context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ItemAddressBinding
        }

        val address = getItem(position)
        binding.apply {
            addressLine1.text = address?.get("addressLine1") as? String ?: ""
            addressLine2.text = address?.get("addressLine2") as? String ?: ""
            itemLandmark.text = address?.get("landmark") as? String ?: ""
            state.text = address?.get("state") as? String ?: ""
            zip.text = address?.get("pincode") as? String ?: ""

            deleteAddress.setOnClickListener {
                deleteAddressFromFirestore(position)
            }

            setDefaultAddress.setOnClickListener {
                setDefaultAddress(position)
            }

            // Determine if the "Set as default" button should be visible
            val isDefault = address?.get("isDefault") as? Boolean ?: false
            updateSetDefaultButtonVisibility(isDefault, setDefaultAddress)
        }

        return binding.root
    }

    private fun updateSetDefaultButtonVisibility(isDefault: Boolean, button: Button) {
        button.visibility = if (isDefault) View.GONE else View.VISIBLE
    }

    private fun setDefaultAddress(position: Int) {
        val userRef = database.collection("USERS").document(email)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val addresses = document.get("addresses") as? MutableList<MutableMap<String, Any>> ?: mutableListOf()

                // Set all addresses isDefault to false, except the selected one
                addresses.forEachIndexed { index, address ->
                    address["isDefault"] = (index == position)
                }

                // Update the document with new address list
                userRef.update("addresses", addresses).addOnSuccessListener {
                    notifyDataSetChanged()  // Refresh the adapter
                    Toast.makeText(context, "Default address updated successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Error updating default address", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteAddressFromFirestore(position: Int) {
        val userRef = database.collection("USERS").document(email)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val userAddresses =
                    document.get("addresses") as? ArrayList<Map<String, String>> ?: arrayListOf()
                if (position < userAddresses.size) {
                    userAddresses.removeAt(position)
                    userRef.update("addresses", userAddresses)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Address deleted successfully")
                            addresses.removeAt(position)
                            notifyDataSetChanged()
                            Toast.makeText(
                                context,
                                "Address deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error deleting address", e)
                            Toast.makeText(context, "Error deleting address", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
        }
    }
}

