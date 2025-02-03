package com.chaitany.agewell.com.chaitany.agewell

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaitany.agewell.HospitalActivity
import com.chaitany.agewell.R

data class Hospital(
    val name: String,
    val displayName: String,
    val lat: Double,
    val lon: Double,
    val placeId: String?,
    val addressType: String?,
    val importance: Double?,
    val boundingBox: List<String>?
)
class HospitalAdapter(private val hospitalList: List<Hospital>, hospitalActivity: HospitalActivity) : RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hospital, parent, false)
        return HospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitalList[position]
        holder.nameTextView.text = hospital.name
        holder.displayNameTextView.text = hospital.displayName
        holder.latTextView.text = "Latitude: ${hospital.lat}"
        holder.lonTextView.text = "Longitude: ${hospital.lon}"
        holder.placeIdTextView.text = "Place ID: ${hospital.placeId ?: "N/A"}"
        holder.addressTypeTextView.text = "Address Type: ${hospital.addressType ?: "N/A"}"
        holder.importanceTextView.text = "Importance: ${hospital.importance ?: "N/A"}"
        holder.boundingBoxTextView.text = "Bounding Box: ${hospital.boundingBox?.joinToString(", ") ?: "N/A"}"

        holder.startNavigatingButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${hospital.lat},${hospital.lon}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            holder.itemView.context.startActivity(mapIntent)
        }
    }

    override fun getItemCount(): Int = hospitalList.size

    class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val displayNameTextView: TextView = itemView.findViewById(R.id.displayNameTextView)
        val latTextView: TextView = itemView.findViewById(R.id.latTextView)
        val lonTextView: TextView = itemView.findViewById(R.id.lonTextView)
        val placeIdTextView: TextView = itemView.findViewById(R.id.placeIdTextView)
        val addressTypeTextView: TextView = itemView.findViewById(R.id.addressTypeTextView)
        val importanceTextView: TextView = itemView.findViewById(R.id.importanceTextView)
        val boundingBoxTextView: TextView = itemView.findViewById(R.id.boundingBoxTextView)
        val startNavigatingButton: Button = itemView.findViewById(R.id.startNavigatingButton)
    }
}
