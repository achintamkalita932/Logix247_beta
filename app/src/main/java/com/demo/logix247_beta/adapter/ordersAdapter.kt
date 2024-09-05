package com.demo.logix247_beta.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.logix247_beta.Models.OnOrderClickListener
import com.demo.logix247_beta.Models.Orders
import com.demo.logix247_beta.R

class ordersAdapter(private var ordersList: List<Orders>, private val listener: OnOrderClickListener) : RecyclerView.Adapter<ordersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View, val listener: OnOrderClickListener) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(order: Orders) {
            itemView.findViewById<TextView>(R.id.productNameTv).text = order.itemDescription
            itemView.findViewById<TextView>(R.id.orderStatus).text = order.orderStatus
            // Bind other views in the item layout

            itemView.setOnClickListener {
                Log.d("RecyclerView", "Item clicked: ${order.id}")
                listener.onOrderClick(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.orders_item, parent, false)
        return OrderViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(ordersList[position])
    }

    override fun getItemCount() = ordersList.size

    fun updateData(newOrders: List<Orders>) {
        ordersList = newOrders
        notifyDataSetChanged() // Notify the RecyclerView to re-render itself with the new data
    }
}