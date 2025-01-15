package com.chaitany.agewell

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BMI_Calculator : AppCompatActivity() {

    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etAge: EditText
    private lateinit var btnCalculateBMI: Button
    private lateinit var etDisplayBMI: EditText
    private lateinit var recommendedAction: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_calculator)

        // Initialize views
        etHeight = findViewById(R.id.et_height)
        etWeight = findViewById(R.id.et_weight)
        etAge = findViewById(R.id.et_age)
        btnCalculateBMI = findViewById(R.id.id_btn_calculate_bmi)
        etDisplayBMI = findViewById(R.id.et_display_bmi)
        recommendedAction = findViewById(R.id.recommended_action)

        // Set click listener for the calculate button
        btnCalculateBMI.setOnClickListener {
            calculateBMI()
        }
    }

    private fun calculateBMI() {
        // Get input values
        val heightText = etHeight.text.toString()
        val weightText = etWeight.text.toString()
        val ageText = etAge.text.toString()

        if (heightText.isNotEmpty() && weightText.isNotEmpty() && ageText.isNotEmpty()) {
            val height = heightText.toFloat() / 100 // Convert height to meters
            val weight = weightText.toFloat()
            val bmi = weight / (height * height) // BMI calculation

            // Display BMI value and category
            val bmiValue = String.format("%.1f", bmi)
            val category = getBMICategory(bmi)
            etDisplayBMI.setText("Your BMI: $bmiValue\nCategory: $category")

            // Display recommendations
            val recommendations = getRecommendations(bmi)
            recommendedAction.text = "Recommended Actions:\n\n" + recommendations.joinToString("\n• ") { it }
        } else {
            etDisplayBMI.setText("Please enter all the details.")
        }
    }

    private fun getBMICategory(bmi: Float): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal weight"
            bmi < 29.9 -> "Overweight"
            else -> "Obese"
        }
    }

    private fun getRecommendations(bmi: Float): List<String> {
        return when {
            bmi < 18.5 -> listOf(
                "Increase calorie intake with nutritious foods",
                "Include strength training to build muscle mass",
                "Consult a nutritionist for a personalized diet plan"
            )
            bmi < 24.9 -> listOf(
                "Maintain your current lifestyle",
                "Continue regular exercise and balanced diet",
                "Schedule periodic health check-ups"
            )
            bmi < 29.9 -> listOf(
                "Adopt portion control and mindful eating",
                "Increase physical activity levels",
                "Consult a healthcare provider for weight management"
            )
            else -> listOf(
                "Start with low-impact exercises",
                "Focus on sustainable dietary changes",
                "Consult a healthcare provider for a detailed plan"
            )
        }
    }
}
