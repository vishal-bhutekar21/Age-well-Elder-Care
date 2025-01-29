package com.chaitany.agewell

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.chaitany.agewell.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.properties.Delegates

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var Otp by Delegates.notNull<Int>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth and SharedPreferences
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserLogin", Context.MODE_PRIVATE)

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {

        }

        // Enable Edge-to-Edge UI
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Set click listener for login button
        binding.btnLoginNow.setOnClickListener {
            if (!binding.btnVerifyOtp.isEnabled && !binding.btnSendOtp.isEnabled) {
                // Get the entered mobile number
                val mobileNumber = binding.etMobileNumber.text.toString().trim()

                if (mobileNumber.isNotEmpty()) {
                    val userRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(mobileNumber)

                    // Create user data in Realtime Database
                    val userData = mapOf(
                        "name" to "",
                        "email" to "",
                        "phone" to mobileNumber
                    )

                    userRef.setValue(userData).addOnSuccessListener {
                        // Store login state in SharedPreferences
                        sharedPreferences.edit().apply {
                            putBoolean("isLoggedIn", true)
                            apply()
                        }

                        // Display success message and navigate to Home
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please Verify Otp", Toast.LENGTH_SHORT).show()
            }
        }


    // Function to navigate to Home activity




binding.btnSendOtp.setOnClickListener {

            if(binding.etMobileNumber.length()==10){
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    val SMS_PERMISSION_CODE=123
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
                }

                sendOtp(binding.etMobileNumber.text.toString())
            }else{
                Toast.makeText(this,"Enter Valid Mobile",Toast.LENGTH_SHORT)
                    .show()
            }



        }
        binding.btnVerifyOtp.setOnClickListener {
            if(binding.etMobileNumber.length()==10 && binding.etOtp.length()==6){

                if(binding.etOtp.text.toString()==Otp.toString()){

                    Toast.makeText(this,"Verification Complete",Toast.LENGTH_SHORT).show()
                    binding.btnVerifyOtp.isEnabled=false

                }else{
                    Toast.makeText(this,"Otp is incorrect $Otp",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Enter Valid Otp and Mobile",Toast.LENGTH_SHORT).show()

            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun sendOtp(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            Otp = (100000..999999).random() // Generate 6-digit OTP
            val message = "Your OTP for login into Age Well : $Otp , Don't Share With Anyone."
            val smsManager = android.telephony.SmsManager.getDefault()

            try {
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(this, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                startOtpTimer(binding.btnSendOtp,binding.tvTimer)




            } catch (e: Exception) {
                Toast.makeText(this, "Failed to send OTP: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "SMS permission is required to send OTP", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(
                    this,
                    "Permission granted! Try sending OTP again.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Permission denied! Can't send SMS.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    fun startOtpTimer(button: Button, timerTextView: TextView) {
        // Disable the button when clicked
        button.isEnabled = false

        // Set up the countdown timer for 30 seconds (30,000 milliseconds)
        val timer = object : CountDownTimer(30000, 1000) { // 30 seconds, 1-second intervals
            override fun onTick(millisUntilFinished: Long) {
                // Update the timer UI every second
                val secondsRemaining = millisUntilFinished / 1000
                timerTextView.text = "Resend Again:$secondsRemaining seconds remaining"
            }

            override fun onFinish() {
                // Re-enable the button when the timer finishes
                button.isEnabled = true
                timerTextView.text = "Send OTP"
            }
        }


        timer.start()
    }

}