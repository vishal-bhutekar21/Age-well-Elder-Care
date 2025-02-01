package com.chaitany.agewell

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Splash : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserLogin", Context.MODE_PRIVATE)

        // Check for necessary permissions
        if (!hasRequiredPermissions()) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.CALL_PHONE
                ),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            // If permissions are already granted, check login status
            checkLoginStatus()
        }

        // Setup window insets for edge-to-edge view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Check if all required permissions are granted
    private fun hasRequiredPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    // Check the login status from SharedPreferences
    private fun checkLoginStatus() {
        val isLoggedIn = sharedPreferences.getBoolean("isLogged", false)

        val handler = Handler()
        handler.postDelayed({
            val intent = if (isLoggedIn) {
                // If user is logged in, navigate to the main activity/dashboard
                Intent(this, Dashboard::class.java)
            } else {
                // If not logged in, navigate to LoginActivity
                Intent(this, Login::class.java)
            }
            startActivity(intent)
            finish() // Finish the SplashActivity so it won't stay in the back stack
        }, 2000) // 2000ms delay for splash screen
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions granted
                checkLoginStatus()
            } else {
                // Some permissions denied, show message to the user
                Toast.makeText(this, "Permissions are required to use the app", Toast.LENGTH_LONG).show()
                // Optionally, you can finish the activity or take further action
                finish() // Forcibly close the splash activity
            }
        }
    }
}
