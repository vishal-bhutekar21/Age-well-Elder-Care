package com.chaitany.agewell;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OurFeedbacks extends AppCompatActivity {

    private RecyclerView feedbackRecyclerView;
    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList;

    private DatabaseReference feedbackRef;
    private ProgressDialog progressDialog; // Declare ProgressDialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_our_feedbacks); // Ensure this matches your layout file

        feedbackRecyclerView = findViewById(R.id.feedback_recycler_view);

        // Initialize Firebase reference
        feedbackRef = FirebaseDatabase.getInstance().getReference("feedback"); // Main feedback node

        // Set up RecyclerView
        feedbackRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        feedbackRecyclerView.setAdapter(feedbackAdapter);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading feedback...");
        progressDialog.setCancelable(false); // Prevent dismissal on back press

        // Fetch feedback data
        fetchFeedbackData();
    }

    private void fetchFeedbackData() {
        progressDialog.show(); // Show the ProgressDialog

        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedbackList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Feedback feedback = snapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        feedbackList.add(feedback); // Add feedback to the list
                    }
                }
                feedbackAdapter.notifyDataSetChanged(); // Notify adapter of data changes
                progressDialog.dismiss(); // Dismiss the ProgressDialog
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss(); // Dismiss the ProgressDialog
                Toast.makeText(OurFeedbacks.this, "Failed to load feedback: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}