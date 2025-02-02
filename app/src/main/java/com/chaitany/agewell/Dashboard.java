package com.chaitany.agewell;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    ImageView useView;
    SharedPreferences sharedPreferences;

    private LinearLayout layoutEmergencyContact, layoutMedicalStock, layoutHealthMonitor, layoutmealplan,
            layout_bmi_index, layout_elder_connect, layout_exercise;
    private LinearLayout morningTasksLayout, afternoonTasksLayout, nightTasksLayout;
    private ImageView menuButton;
    private DatabaseReference tasksRef;
    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
        userPhone = sharedPreferences.getString("mobile", null);

        // Initialize Firebase
        if (userPhone != null) {
            tasksRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(userPhone).child("tasks");
        }

        // Handling window insets for a smooth UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.miana), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupNavigationDrawer();
        setupUserProfile();
        setupClickListeners();
        setupTaskSections();
        loadTasks();
    }

    private void initializeViews() {
        useView=findViewById(R.id.user_png_dot);
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menubars);
        layout_bmi_index = findViewById(R.id.layout_bmi_index);
        layoutEmergencyContact = findViewById(R.id.layout_emergency_contact);
        layoutMedicalStock = findViewById(R.id.layout_medical_stock);
        layoutHealthMonitor = findViewById(R.id.layout_health_monitor);
        layoutmealplan = findViewById(R.id.mealplans);
        layout_elder_connect = findViewById(R.id.layout_elder_connect);
        layout_exercise = findViewById(R.id.layoutexercise);

        // Initialize task section layouts
        morningTasksLayout = findViewById(R.id.morningTasksLayout);
        afternoonTasksLayout = findViewById(R.id.afternoonTasksLayout);
        nightTasksLayout = findViewById(R.id.nightTasksLayout);
    }

    private void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupUserProfile() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);

        SharedPreferences sharedPref = getSharedPreferences("UserLogin", MODE_PRIVATE);
        String name = sharedPref.getString("name", "User Name");
        String mobile = sharedPref.getString("mobile", "XXXX XX XXXX");
        String gender = sharedPref.getString("gender", "Gender");

        TextView username = findViewById(R.id.username);
        ImageView userimg = findViewById(R.id.user_png_dot);
        TextView usernameTextView = headerView.findViewById(R.id.username);
        TextView mobileTextView = headerView.findViewById(R.id.mobile);
        ImageView user_png = headerView.findViewById(R.id.user_png);

        if (gender.equals("Male")) {
            user_png.setImageResource(R.drawable.img_oldman);
            userimg.setImageResource(R.drawable.img_oldman);
        }
        if (gender.equals("Female")) {
            user_png.setImageResource(R.drawable.img_oldfemale);
            userimg.setImageResource(R.drawable.img_oldfemale);
        }

        username.setText("Hi, " + name);
        usernameTextView.setText(name);
        mobileTextView.setText(mobile);
    }



    private void setupTaskSections() {
        if (morningTasksLayout != null) {
            setupTaskSection(morningTasksLayout, "Morning");
        }
        if (afternoonTasksLayout != null) {
            setupTaskSection(afternoonTasksLayout, "Afternoon");
        }
        if (nightTasksLayout != null) {
            setupTaskSection(nightTasksLayout, "Night");
        }
    }

    private void setupTaskSection(LinearLayout layout, final String timeOfDay) {
        // Find the TextView within the layout (second child)
        TextView titleText = (TextView) ((LinearLayout) layout).getChildAt(1);

        // Create content container
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 8, 32, 8);
        contentLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        contentLayout.setVisibility(View.GONE);

        // Add content layout after the header
        ViewGroup parent = (ViewGroup) layout.getParent();
        parent.addView(contentLayout, parent.indexOfChild(layout) + 1);

        // Set up click listener
        layout.setOnClickListener(v -> {
            boolean isExpanded = contentLayout.getVisibility() == View.VISIBLE;
            // Toggle visibility with animation
            if (isExpanded) {
                contentLayout.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> contentLayout.setVisibility(View.GONE))
                        .start();
            } else {
                contentLayout.setAlpha(0f);
                contentLayout.setVisibility(View.VISIBLE);
                contentLayout.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .start();
            }

            // Rotate arrow
            titleText.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.down_icon,
                    0
            );

            // Animate arrow rotation
            Drawable[] drawables = titleText.getCompoundDrawables();
            if (drawables[2] != null) {
                drawables[2].setLevel(isExpanded ? 0 : 10000);
                ObjectAnimator.ofInt(drawables[2], "level", isExpanded ? 10000 : 0)
                        .setDuration(200)
                        .start();
            }
        });
    }

    private void loadTasks() {
        if (tasksRef == null) return;

        tasksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearAllTasks();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        addTaskToSection(task);
                    }
                }
                // Add predefined tasks after loading tasks from Firebase
                addPredefinedTasks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Failed to load tasks: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void clearAllTasks() {
        clearTaskSection(morningTasksLayout);
        clearTaskSection(afternoonTasksLayout);
        clearTaskSection(nightTasksLayout);
    }

    private void clearTaskSection(LinearLayout sectionLayout) {
        ViewGroup parent = (ViewGroup) sectionLayout.getParent();
        int index = parent.indexOfChild(sectionLayout);
        if (index + 1 < parent.getChildCount()) {
            View contentView = parent.getChildAt(index + 1);
            if (contentView instanceof LinearLayout) {
                ((LinearLayout) contentView).removeAllViews();
            }
        }
    }

    private void addTaskToSection(Task task) {
        LinearLayout targetSection2;
        String sectionTime = task.getTime().toLowerCase();
        ViewGroup parent;

        // Determine the target section based on the task time
        if (sectionTime.contains("morning")) {
            parent = (ViewGroup) morningTasksLayout.getParent();
            targetSection2 = (LinearLayout) parent.getChildAt(parent.indexOfChild(morningTasksLayout) + 1);
        } else if (sectionTime.contains("afternoon")) {
            parent = (ViewGroup) afternoonTasksLayout.getParent();
            targetSection2 = (LinearLayout) parent.getChildAt(parent.indexOfChild(afternoonTasksLayout) + 1);
        } else if (sectionTime.contains("night")) {
            parent = (ViewGroup) nightTasksLayout.getParent();
            targetSection2 = (LinearLayout) parent.getChildAt(parent.indexOfChild(nightTasksLayout) + 1);
        } else {
            targetSection2 = null;
        }

        if (targetSection2 != null) {
            final View taskView = getLayoutInflater().inflate(R.layout.item_task, targetSection2, false);

            // Add margin between items
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 4, 0, 4);
            taskView.setLayoutParams(params);

            TextView medicineNameText = taskView.findViewById(R.id.tvMedicineName);
            TextView mealTimeText = taskView.findViewById(R.id.tvMealTime);
            TextView stockValueText = taskView.findViewById(R.id.tvStockValue);
            CheckBox completeTaskCheckBox = taskView.findViewById(R.id.cbCompleteTask);

            medicineNameText.setText(task.getMedicineName());
            mealTimeText.setText(task.getMealTime());
            stockValueText.setText(task.getQuantity() + " tablets");

            // Set up checkbox listener with Firebase integration
            completeTaskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    new AlertDialog.Builder(Dashboard.this)
                            .setTitle("Task Completion")
                            .setMessage("Are you sure you want to mark this task as completed?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Decrease the quantity locally first
                                int newQuantity = task.getQuantity() - 1;
                                if (newQuantity >= 0) {
                                    task.setQuantity(newQuantity);
                                    tasksRef.child(task.getMedicineName()).child("quantity")
                                            .setValue(newQuantity)
                                            .addOnSuccessListener(aVoid -> {
                                                stockValueText.setText(newQuantity + " tablets");
                                                if (newQuantity == 0) {
                                                    targetSection2.removeView(taskView);
                                                }
                                                Toast.makeText(Dashboard.this,
                                                        "Task completed! Remaining tablets: " + newQuantity,
                                                        Toast.LENGTH_SHORT).show();
                                                completeTaskCheckBox.setChecked(false);
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(Dashboard.this,
                                                        "Failed to update quantity: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                                completeTaskCheckBox.setChecked(false);
                                            });
                                } else {
                                    Toast.makeText(Dashboard.this, "No tablets left!", Toast.LENGTH_SHORT).show();
                                    completeTaskCheckBox.setChecked(false);
                                }
                            })
                            .setNegativeButton("No", (dialog, which) -> completeTaskCheckBox.setChecked(false))
                            .show();
                }
            });

            targetSection2.addView(taskView);
        }
    }
    // New method to add predefined tasks based on elderly activities
    private void addPredefinedTasks() {
        // Predefined tasks for morning, afternoon, and night
        addPredefinedTaskToSection(new Task("Jogging", "morning", "Morning", 1));
        addPredefinedTaskToSection(new Task("Yoga", "morning", "Morning", 1));
        addPredefinedTaskToSection(new Task("Exercise", "morning", "Morning", 1));
        addPredefinedTaskToSection(new Task("Nap", "afternoon", "Afternoon", 1));
        addPredefinedTaskToSection(new Task("Walk", "afternoon", "Afternoon", 1));
        addPredefinedTaskToSection(new Task("Lunch", "afternoon", "Afternoon", 1));
        addPredefinedTaskToSection(new Task("Dinner", "night", "Night", 1));
        addPredefinedTaskToSection(new Task("Evening Walk", "night", "Night", 1));
        addPredefinedTaskToSection(new Task("Reading", "night", "Night", 1));
    }

    private void addPredefinedTaskToSection(Task task) {
        final LinearLayout targetSection; // Declare as final
        String sectionTime = task.getTime().toLowerCase();
        ViewGroup parent;

        // Determine the target section based on the task time
        if (sectionTime.contains("morning")) {
            parent = (ViewGroup) morningTasksLayout.getParent();
            targetSection = (LinearLayout) parent.getChildAt(parent.indexOfChild(morningTasksLayout) + 1);
        } else if (sectionTime.contains("afternoon")) {
            parent = (ViewGroup) afternoonTasksLayout.getParent();
            targetSection = (LinearLayout) parent.getChildAt(parent.indexOfChild(afternoonTasksLayout) + 1);
        } else if (sectionTime.contains("night")) {
            parent = (ViewGroup) nightTasksLayout.getParent();
            targetSection = (LinearLayout) parent.getChildAt(parent.indexOfChild(nightTasksLayout) + 1);
        } else {
            return; // If no valid section is found, exit the method
        }

        if (targetSection != null) {
            View taskView = getLayoutInflater().inflate(R.layout.item_predefined_task, targetSection, false);

            // Add margin between items
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 4, 0, 4);
            taskView.setLayoutParams(params);

            TextView taskNameText = taskView.findViewById(R.id.tvPredefinedTaskName);
            TextView taskTimeText = taskView.findViewById(R.id.tvPredefinedTaskTime);
            CheckBox completeTaskCheckBox = taskView.findViewById(R.id.cbCompleteTask);

            taskNameText.setText(task.getMedicineName());
            taskTimeText.setText(task.getMealTime());

            // Set up checkbox listener
            completeTaskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Show confirmation dialog
                    new AlertDialog.Builder(Dashboard.this)
                            .setTitle("Task Completion")
                            .setMessage("Are you sure you want to mark this task as completed?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                // Remove the task from the target section
                                targetSection.removeView(taskView);
                                // Show a toast message
                                Toast.makeText(Dashboard.this, "Task completed!", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                // Uncheck the checkbox if the user cancels
                                completeTaskCheckBox.setChecked(false);
                            })
                            .show();
                }
            });

            targetSection.addView(taskView);
        }
    }

    private void setupClickListeners() {

        useView.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, ProfileActivity.class)));
        layout_bmi_index.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, BMIActivity.class)));
        layoutEmergencyContact.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, EmergencyContact.class)));
        layout_elder_connect.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, globalchat.class)));
        layoutMedicalStock.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, MedicalStockActivity.class)));
        layoutHealthMonitor.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, ActivityAppointment.class)));
        layoutmealplan.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, MealPlanner.class)));
        layout_exercise.setOnClickListener(v -> startActivity(new Intent(Dashboard.this, Exercise_suggestion.class)));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;

        if (item.getItemId() == R.id.nav_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLogged", false);
            editor.apply();
            intent = new Intent(Dashboard.this, Login.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_profile) {
            intent = new Intent(Dashboard.this, ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_share) {
            shareApp();
        } else if (item.getItemId() == R.id.nav_feedback) {
            intent = new Intent(Dashboard.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.nav_about) {
            intent = new Intent(Dashboard.this, AboutActivity.class);
            startActivity(intent);
        } else {
            return false;
        }






        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void shareApp() {
        String appLink = "https://play.google.com/store/apps/details?id=com.chaitany.agewell";
        String shareMessage = "Check out this amazing app that helps you and your older person to stay healthy and care! \n\n" +
                "Download it here: " + appLink + "\n\n" +
                "Stay healthy and fit!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Our App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Task model class
    public static class Task {
        private String medicineName;
        private String time;
        private String mealTime;
        private int quantity;

        // Required empty constructor for Firebase
        public Task() {}

        // Constructor for predefined tasks
        public Task(String medicineName, String time, String mealTime, int quantity) {
            this.medicineName = medicineName;
            this.time = time;
            this.mealTime = mealTime;
            this.quantity = quantity;
        }

        // Getters and setters
        public String getMedicineName() {
            return medicineName;
        }

        public void setMedicineName(String medicineName) {
            this.medicineName = medicineName;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMealTime() {
            return mealTime;
        }

        public void setMealTime(String mealTime) {
            this.mealTime = mealTime;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}