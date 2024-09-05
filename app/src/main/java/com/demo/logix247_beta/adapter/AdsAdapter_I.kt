package com.demo.logix247_beta.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.demo.logix247_beta.Models.AdsI
import com.demo.logix247_beta.R

class AdsAdapter_I(private val adsList: List<AdsI>): RecyclerView.Adapter<AdsAdapter_I.AdsViewHolder>() {

    class AdsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val adsImageView: ImageView = itemView.findViewById(R.id.adsImageView)
        fun bind(ads: AdsI){
            adsImageView.setImageResource(ads.adsImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ads_item, parent, false)
        return AdsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adsList.size
    }

    override fun onBindViewHolder(holder: AdsViewHolder, position: Int) {
        val ads = adsList[position]
        holder.adsImageView.setImageResource(ads.adsImage)
    }
}