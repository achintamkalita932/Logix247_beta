package com.demo.logix247_beta.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.logix247_beta.Models.OnPartnerClickListener
import com.demo.logix247_beta.Models.PartnerDetails
import com.demo.logix247_beta.R

class PartnerAdapter(
    var partners: List<PartnerDetails>,
    private val listener: OnPartnerClickListener
) : RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder>() {

    inner class PartnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val companyName: TextView = itemView.findViewById(R.id.tvCompanyName)
        val rating: TextView = itemView.findViewById(R.id.tvRating)

        // Add other views if necessary
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPartnerClick(partners[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PartnerAdapter.PartnerViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.partner_item, parent, false)
        return PartnerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PartnerAdapter.PartnerViewHolder, position: Int) {
        val partner: PartnerDetails = partners[position]
        holder.companyName.text = partner.companyName
//        holder.range.text = partner.range
        holder.rating.text = partner.rating

        // Bind other views if necessary
    }

    override fun getItemCount(): Int {
        return partners.size
    }

    fun updateData(newPartners: List<PartnerDetails>) {
        this.partners = newPartners
        notifyDataSetChanged()
    }
}