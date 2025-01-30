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
    private var DEFAULT_PHONE: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserLogin", Context.MODE_PRIVATE)
        DEFAULT_PHONE = sharedPreferences.getString("mobile", null)

        if (DEFAULT_PHONE.isNullOrEmpty()) {
            Toast.makeText(this, "User phone number not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddMedicine.setOnClickListener {
            saveMedicine()
        }
    }

    private fun saveMedicine() {
        val name = binding.etMedicineName.text.toString().trim()
        val type = binding.etMedicineType.text.toString().trim()
        val quantityString = binding.etQuantity.text.toString().trim()

        if (name.isEmpty() || type.isEmpty() || quantityString.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityString.toIntOrNull() ?: 0
        if (quantity <= 0) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
            return
        }

        val schedule = mutableListOf<String>()
        if (binding.chipMorning.isChecked) schedule.add("Morning")
        if (binding.chipAfternoon.isChecked) schedule.add("Afternoon")
        if (binding.chipNight.isChecked) schedule.add("Night")

        val mealTime = when {
            binding.chipBeforeLunch.isChecked -> "Before Lunch"
            binding.chipAfterLunch.isChecked -> "After Lunch"
            else -> null
        }

        val medicineRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(DEFAULT_PHONE!!)
            .child("medicines")
            .push()

        val medicineData = hashMapOf(
            "name" to name,
            "type" to type,
            "quantity" to quantity,
            "schedule" to schedule,
            "mealTime" to mealTime,
            "userPhone" to DEFAULT_PHONE
        )

        medicineRef.setValue(medicineData)
            .addOnSuccessListener {
                Toast.makeText(this, "Medicine saved successfully", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
