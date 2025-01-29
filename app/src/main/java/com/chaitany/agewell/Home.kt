package com.chaitany.agewell

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private  lateinit var layout_emergency_contact: LinearLayout
    private  lateinit var layout_medical_stock: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize the DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)

        layout_emergency_contact.setOnClickListener {
            var intent=Intent(this,Emergency_Contacts::class.java)
            startActivity(intent)

        }
        layout_medical_stock.setOnClickListener {
            var intent=Intent(this,MedicalStockActivity::class.java)
            startActivity(intent)

        }


        // Set up the ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable hamburger button click
        findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up NavigationView listener
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
            R.id.nav_profile -> Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show()
            R.id.nav_logout -> Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            R.id.nav_share -> Toast.makeText(this, "Share selected", Toast.LENGTH_SHORT).show()
            R.id.nav_feedback -> Toast.makeText(this, "Feedback selected", Toast.LENGTH_SHORT).show()
            R.id.nav_about -> Toast.makeText(this, "About selected", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
