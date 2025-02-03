package com.chaitany.agewell.com.chaitany.agewell

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Hospital(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

class HospitalAdapter(private val hospitalList: List<Hospital>, private val context: Context) :
    RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hospital, parent, false)
        return HospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitalList[position]
        holder.nameTextView.text = hospital.name
        holder.addressTextView.text = hospital.address

        // Start navigation when button is clicked
        holder.navigateButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${hospital.latitude},${hospital.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }
    }

    override fun getItemCount() = hospitalList.size

    class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.hospital_name)
        val addressTextView: TextView = itemView.findViewById(R.id.hospital_address)
        val navigateButton: Button = itemView.findViewById(R.id.start_navigation_button)
    }
}
