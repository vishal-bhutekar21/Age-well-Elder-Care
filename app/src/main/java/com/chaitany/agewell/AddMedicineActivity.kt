package com.chaitany.agewell

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.chaitany.agewell.databinding.ActivityAddMedicineBinding
import com.google.firebase.database.FirebaseDatabase

class AddMedicineActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMedicineBinding
    private var DEFAULT_PHONE: String? = null  // Store user's phone number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserLogin", Context.MODE_PRIVATE)
        DEFAULT_PHONE = sharedPreferences.getString("mobile", null)

        // Check if mobile number is retrieved
        if (DEFAULT_PHONE.isNullOrEmpty()) {
            Toast.makeText(this, "User phone number not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Inflate UI
        binding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle back button click
        binding.btnBack.setOnClickListener {
            onBackPressed()  // Navigate back
        }

        // Add Medicine button click listener
        binding.btnAddMedicine.setOnClickListener {
            saveMedicine()
        }
    }

    private fun saveMedicine() {
        // Get values from input fields
        val name = binding.etMedicineName.text.toString().trim()
        val type = binding.etMedicineType.text.toString().trim()
        val quantityString = binding.etQuantity.text.toString().trim()

        // Validate inputs
        if (name.isEmpty() || type.isEmpty() || quantityString.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate quantity
        val quantity = quantityString.toIntOrNull()
        if (quantity == null || quantity <= 0) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected schedule times
        val schedule = mutableListOf<String>()
        if (binding.chipMorning.isChecked) schedule.add("Morning")
        if (binding.chipAfternoon.isChecked) schedule.add("Afternoon")
        if (binding.chipNight.isChecked) schedule.add("Night")

        if (schedule.isEmpty()) {
            Toast.makeText(this, "Please select at least one schedule time", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique ID for the medicine
        val medicineRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(DEFAULT_PHONE!!)  // Use phone number as user ID
            .child("medicines")
            .push()

        val medicineId = medicineRef.key  // Firebase generates a unique key

        // Create medicine data with the generated ID
        val medicineData = hashMapOf(
            "id" to medicineId,
            "name" to name,
            "type" to type,
            "quantity" to quantity,
            "schedule" to schedule,
            "userPhone" to DEFAULT_PHONE
        )

        // Save to Firebase
        medicineRef.setValue(medicineData)
            .addOnSuccessListener {
                Toast.makeText(this, "Medicine saved successfully", Toast.LENGTH_LONG).show()
                finish()  // Close the activity when data is saved successfully
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
