package com.chaitany.agewell;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private LinearLayout layoutEmergencyContact, layoutMedicalStock, layoutHealthMonitor, layoutmealplan, layout_bmi_index,layout_elder_connect;
    private ImageView menuButton; // ImageView for opening drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Handling window insets for a smooth UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.miana), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menubars); // Find the ImageView for the menu button
        layout_bmi_index = findViewById(R.id.layout_bmi_index);
        layoutEmergencyContact = findViewById(R.id.layout_emergency_contact);
        layoutMedicalStock = findViewById(R.id.layout_medical_stock);
        layoutHealthMonitor = findViewById(R.id.layout_health_monitor);
        layoutmealplan = findViewById(R.id.mealplans);
        layout_elder_connect = findViewById(R.id.layout_elder_connect);


        // Setup Navigation Drawer
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Open Drawer when clicking the ImageView (menuButton)
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set Navigation View Listener
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        // Handle Button Clicks
        layout_bmi_index.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, BMIActivity.class);
            startActivity(intent);
        });

        layoutEmergencyContact.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, EmergencyContact.class);
            startActivity(intent);
        });
        layout_elder_connect.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, globalchat.class);
            startActivity(intent);
        });

        layoutMedicalStock.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, MedicalStockActivity.class);
            startActivity(intent);
        });

        layoutHealthMonitor.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, ActivityAppointment.class);
            startActivity(intent);
        });

        layoutmealplan.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, MealPlanner.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}