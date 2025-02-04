package com.chaitany.agewell;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import android.widget.Button;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private OnboardingAdapter onboardingAdapter;
    private Button btnNext, btnSkip;
    private int currentPage = 0;
    private SharedPreferences sharedPreferences;

    private final int[] images = {
            R.drawable.imgonboard1,
            R.drawable.imgonboard2,
            R.drawable.imgonboard3
    };

    private final String[] titles = {
            "Help at Your Fingertips!",
            "Never Miss a Dose or Checkup!",
            "A Smarter Way to Care for Those Who Matter Most!"
    };

    private final String[] descriptions = {
            "One-tap SOS alert to caregivers & real-time emergency support.",
            "Smart medication reminders, BMI tracking, and doctor visit alerts.",
            "Smart technology meets heartfelt care – ensuring safety, health, and well-being for seniors, effortlessly."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get SharedPreferences
        sharedPreferences = getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLogged", false);

        // If user is logged in, skip onboarding and go to the main screen (Dashboard)
        if (isLoggedIn) {
            startActivity(new Intent(this, Dashboard.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_on_boarding);

        // Change Status Bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.teal_50)); // Change to your desired color
        }

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);
        btnSkip = findViewById(R.id.btnSkip);

        // Initialize adapter
        onboardingAdapter = new OnboardingAdapter(this, images, titles, descriptions);
        viewPager.setAdapter(onboardingAdapter);

        btnNext.setOnClickListener(v -> {
            if (currentPage < images.length - 1) {
                currentPage++;
                viewPager.setCurrentItem(currentPage);
            } else {
                finishOnboarding();
            }
        });

        btnSkip.setOnClickListener(v -> finishOnboarding());

    }

    // Change navigation bar color
    private void finishOnboarding() {
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
