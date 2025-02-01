package com.chaitany.agewell

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.yourapp.utils.UserPreferences

class Login : AppCompatActivity() {

    private lateinit var mobileEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var progressDialog: ProgressDialog

    private lateinit var databaseReference: DatabaseReference
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        mobileEditText = findViewById(R.id.etMobileNumber)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLoginNow)
        signUpButton = findViewById(R.id.btnSignUp)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        // Initialize ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging in...")
        progressDialog.setCancelable(false)

        // Initialize UserPreferences for saving mobile number
        userPreferences = UserPreferences(this)

        // Login button click listener
        loginButton.setOnClickListener {
            val mobile = mobileEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validation
            if (TextUtils.isEmpty(mobile)) {
                mobileEditText.error = "Mobile number is required"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                passwordEditText.error = "Password is required"
                return@setOnClickListener
            }

            // Show ProgressDialog
            progressDialog.show()

            // Call login function
            loginUser(mobile, password)
        }

        // Sign up button click listener
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(mobile: String, password: String) {
        // Check if user exists in the database and password matches
        databaseReference.child(mobile).get().addOnCompleteListener { task ->
            progressDialog.dismiss()
            if (task.isSuccessful) {
                val user = task.result
                if (user != null && user.exists()) {
                    val storedPassword = user.child("password").value.toString()
                    if (storedPassword == password) {
                        // Successful login, save mobile number to SharedPreferences
                        val sharedPref = getSharedPreferences("UserLogin", MODE_PRIVATE)
                        val editor = sharedPref.edit()

                        // Store mobile number
                        editor.putString("mobile", mobile)

                        // Store isLogged as true
                        editor.putBoolean("isLogged", true)

                        // Apply changes to SharedPreferences
                        editor.apply()
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        // Proceed to next activity
                        val intent = Intent(this, Dashboard::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Login Failed. Please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
