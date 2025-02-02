package com.chaitany.agewell;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    SharedPreferences sharedPreferences;

    private LinearLayout layoutEmergencyContact, layoutMedicalStock, layoutHealthMonitor, layoutmealplan, layout_bmi_index,layout_elder_connect,layout_exercise;
    private ImageView menuButton; // ImageView for opening drawer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
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
        layout_exercise=findViewById(R.id.layoutexercise);
        ImageView userimg=findViewById(R.id.user_png_dot);
        TextView username= findViewById(R.id.username);


        // Setup Navigation Drawer
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Open Drawer when clicking the ImageView (menuButton)
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set Navigation View Listener
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserLogin", MODE_PRIVATE);
        String name = sharedPref.getString("name", "User Name");
        String mobile = sharedPref.getString("mobile", "XXXX XX XXXX");
        String gender=sharedPref.getString("gender","Gender");

        // Find the TextViews in the header layout
        TextView usernameTextView = headerView.findViewById(R.id.username);
        TextView mobileTextView = headerView.findViewById(R.id.mobile);
        ImageView user_png=headerView.findViewById(R.id.user_png);

        if(gender.equals("Male")){
          user_png.setImageResource(R.drawable.img_oldman);
          userimg.setImageResource(R.drawable.img_oldman);
        }
        if(gender.equals("Female")){
            user_png.setImageResource(R.drawable.img_oldfemale);
            userimg.setImageResource(R.drawable.img_oldfemale);
        }


        // Set the user details in the TextViews


        username.setText("Hi, "+name);
        usernameTextView.setText(name);
        mobileTextView.setText(mobile);


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
        layout_exercise.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Exercise_suggestion.class);
            startActivity(intent);
        });
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;

        if (item.getItemId() == R.id.nav_logout) {
            // Clear login status in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLogged", false);
            editor.apply();
                // putString("mobile", null); // Clear mobile number if stored

            // Start the Login activity
            intent = new Intent(Dashboard.this, Login.class);
            startActivity(intent);
            finish(); // Close the current activity
        } else if (item.getItemId() == R.id.nav_profile) {
            intent = new Intent(Dashboard.this, ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_share) {
            String appLink = "https://play.google.com/store/apps/details?id=com.chaitany.agewell"; // Replace with your app's link
            String shareMessage = "Check out this amazing app that helps you and your older person to stay healthy and care! \n\n" +
                    "Download it here: " + appLink + "\n\n" +
                    "Stay healthy and fit!";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Our App");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else if (item.getItemId() == R.id.nav_feedback) {
            intent = new Intent(Dashboard.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_about) {
            intent = new Intent(Dashboard.this, AboutActivity.class);
            startActivity(intent);
        } else {
            return false; // If the item ID does not match any case, return false
        }

        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true; // Indicate that the item selection was handled
    }
}
