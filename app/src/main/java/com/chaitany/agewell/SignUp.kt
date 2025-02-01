package com.chaitany.agewell

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chaitany.agewell.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class SignUp : AppCompatActivity() {

    private lateinit var nameField: TextInputEditText
    private lateinit var mobileField: TextInputEditText
    private lateinit var ageField: TextInputEditText
    private lateinit var genderField: TextInputEditText
    private lateinit var locationField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var signupButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private val REQUEST_CODE_SEND_SMS = 101

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize views
        nameField = findViewById(R.id.nameField)
        mobileField = findViewById(R.id.mobileField)
        ageField = findViewById(R.id.ageField)
        genderField = findViewById(R.id.genderField)
        locationField = findViewById(R.id.locationField)
        passwordField = findViewById(R.id.passwordField)
        signupButton = findViewById(R.id.signupButton)
        progressBar = findViewById(R.id.progressBar)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        signupButton.setOnClickListener {
            val name = nameField.text.toString()
            val mobile = mobileField.text.toString()
            val age = ageField.text.toString()
            val gender = genderField.text.toString()
            val location = locationField.text.toString()
            val password = passwordField.text.toString()

            if (validateFields(name, mobile, age, gender, location, password)) {
                progressBar.visibility = View.VISIBLE
                val otp = generateOtp()
                sendOtpToUser(mobile, otp) // Send OTP to user
                openOtpActivity(mobile, otp, name, age, gender, location, password) // Open OTP activity
            }
        }
    }

    private fun generateOtp(): String {
        // Generate a random 6-digit OTP
        return Random.nextInt(100000, 999999).toString()
    }

    private fun sendOtpToUser(mobile: String, otp: String) {
        // Check if the permission is granted for SEND_SMS
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is granted, proceed to send OTP
            sendSms(mobile, otp)
        } else {
            // If permission is not granted, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.SEND_SMS),
                REQUEST_CODE_SEND_SMS
            )
        }
    }

    private fun sendSms(mobile: String, otp: String) {
        try {
            // Create the message to send
            val message = "Your OTP is: $otp"

            // Send SMS using SmsManager
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(mobile, null, message, null, null)

            // Show toast to notify the user that OTP has been sent
            Toast.makeText(this, "OTP sent to $mobile", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Handle exception in case of errors
            Toast.makeText(this, "Failed to send OTP: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If permission granted, send OTP
                    val mobile = mobileField.text.toString()
                    val otp = generateOtp()
                    sendOtpToUser(mobile, otp)
                } else {
                    // If permission denied, show a message
                    Toast.makeText(this, "Permission to send SMS is required", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openOtpActivity(
        mobile: String,
        otp: String,
        name: String,
        age: String,
        gender: String,
        location: String,
        password: String
    ) {
        // Intent to start the OTP activity where the user will enter the OTP
        val intent = Intent(this, OTP::class.java).apply {
            putExtra("otp", otp)
            putExtra("mobile", mobile)
            putExtra("name", name)
            putExtra("age", age)
            putExtra("gender", gender)
            putExtra("location", location)
            putExtra("password", password)
        }
        startActivity(intent)
    }

    private fun validateFields(
        name: String,
        mobile: String,
        age: String,
        gender: String,
        location: String,
        password: String
    ): Boolean {
        // Basic validation
        if (name.isEmpty() || mobile.isEmpty() || age.isEmpty() || gender.isEmpty() || location.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return false
        }

        // Validate mobile number (basic check for length)
        if (mobile.length != 10) {
            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return false
        }

        // Validate age (ensure it's a number)
        try {
            val ageInt = age.toInt()
            if (ageInt <= 0) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return false
        }

        return true
    }
}
