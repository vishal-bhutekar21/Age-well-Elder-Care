package com.chaitany.agewell

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaitany.agewell.com.chaitany.agewell.Hospital
import com.chaitany.agewell.com.chaitany.agewell.HospitalAdapter
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class HospitalActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var hospitalAdapter: HospitalAdapter
    private var hospitalList: MutableList<Hospital> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hospital)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        hospitalAdapter = HospitalAdapter(hospitalList, this)
        recyclerView.adapter = hospitalAdapter

        // Request location permissions and get the current location
        getLocationAndFetchHospitals()
    }

    private fun getLocationAndFetchHospitals() {
        // Get user's current location using FusedLocationProviderClient or any location service
        val latitude = 37.7749 // Example latitude (San Francisco)
        val longitude = -122.4194 // Example longitude (San Francisco)

        // Now, call the method to get nearby hospitals
        getNearbyHospitals(latitude, longitude)
    }

    private fun getNearbyHospitals(latitude: Double, longitude: Double) {
        val url = "https://nominatim.openstreetmap.org/search?q=hospital&format=json&lat=$latitude&lon=$longitude&radius=5000"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.let { body ->
                    val jsonResponse = JSONObject(body.string())
                    val hospitals = jsonResponse.getJSONArray("results")
                    for (i in 0 until hospitals.length()) {
                        val hospital = hospitals.getJSONObject(i)
                        val hospitalName = hospital.getString("name")
                        val hospitalAddress = hospital.optString("vicinity", "Address not available")
                        val hospitalLat = hospital.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                        val hospitalLng = hospital.getJSONObject("geometry").getJSONObject("location").getDouble("lng")

                        // Add hospital to list
                        val newHospital = Hospital(hospitalName, hospitalAddress, hospitalLat, hospitalLng)
                        hospitalList.add(newHospital)
                    }

                    // Update RecyclerView with new data on the main thread
                    runOnUiThread {
                        hospitalAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
