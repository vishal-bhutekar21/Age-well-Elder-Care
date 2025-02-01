package com.chaitany.agewell;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Exercise_suggestion extends AppCompatActivity {

    private RecyclerView exerciseRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private LinearProgressIndicator progressBar;
    private TextView progressText;
    private ExtendedFloatingActionButton addExerciseFab;
    private List<Exercise> exerciseList; // Local storage for exercises
    private int completedExercises = 0; // Track completed exercises
    private TextView timerTextView; // TextView to display remaining time
    private TextToSpeech textToSpeech; // TextToSpeech instance
    private boolean isExerciseInProgress = false; // Track if an exercise is currently in progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_suggestion);

        initializeViews();
        setupRecyclerView();
        exerciseList = new ArrayList<>(); // Initialize the exercise list here
        initializeTextToSpeech(); // Initialize TextToSpeech
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbarexercise);
        setSupportActionBar(toolbar);

        exerciseRecyclerView = findViewById(R.id.exerciseRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        addExerciseFab = findViewById(R.id.addExerciseFab);
        timerTextView = findViewById(R.id.timerTextView); // Initialize the timer TextView

        addExerciseFab.setOnClickListener(v -> showAddExerciseDialog());
    }

    private void setupRecyclerView() {
        exerciseAdapter = new ExerciseAdapter(this);
        exerciseRecyclerView.setAdapter(exerciseAdapter);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateProgress() {
        double progress = (completedExercises * 100.0) / exerciseList.size();
        progressBar.setProgress((int) progress);
        progressText.setText(String.format("%.0f%% Complete", progress));
    }

    public void startExercise(Exercise exercise) {
        if (isExerciseInProgress) {
            Toast.makeText(this, "Finish the current exercise before starting a new one.", Toast.LENGTH_SHORT).show();
            return;
        }

        isExerciseInProgress = true; // Set the flag to indicate an exercise is in progress
        int totalTime = exercise.getSets() * exercise.getReps() * exercise.getRestTime() * 1000; // Total time in milliseconds

        timerTextView.setVisibility(View.VISIBLE); // Make sure the timer is visible

        new CountDownTimer(totalTime, 1000) { // Update every second
            int timeLeft = totalTime / 1000; // Convert milliseconds to seconds

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft--; // Decrement the time left
                timerTextView.setText(String.format("Time Left: %d seconds", timeLeft));

                if (timeLeft % 10 == 0) {
                    announceRemainingTime(timeLeft);
                }
            }

            @Override
            public void onFinish() {
                completedExercises++;
                updateProgress(); // Update progress based on completed exercises
                showExerciseCompletedDialog(exercise);
                timerTextView.setVisibility(View.GONE); // Hide the timer when finished
                isExerciseInProgress = false; // Reset the flag when the exercise is finished
            }
        }.start();
    }

    private void announceRemainingTime(int timeLeft) {
        String message = "Time left: " + timeLeft + " seconds";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void showExerciseCompletedDialog(Exercise exercise) {
        new AlertDialog.Builder(this)
                .setTitle("Exercise Completed")
                .setMessage("You have completed " + exercise.getName() + "!")
                .setPositiveButton("OK", null)
                .show();
    }

    public void watchExerciseVideo(Exercise exercise) {
        String videoUrl = exercise.getVideoUrl(); // Get the video URL from the exercise object
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        startActivity(intent);
    }

    private void showAddExerciseDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_exercise);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextInputEditText nameInput = dialog.findViewById(R.id.exerciseNameInput);
        TextInputEditText setsInput = dialog.findViewById(R.id.setsInput);
        TextInputEditText repsInput = dialog.findViewById(R.id.repsInput);
        TextInputEditText restTimeInput = dialog.findViewById(R.id.restTimeInput);
        TextInputEditText instructionsInput = dialog.findViewById(R.id.instructionsInput);
        TextInputEditText safetyTipsInput = dialog.findViewById(R.id.safetyTipsInput);
        TextInputEditText videoUrlInput = dialog.findViewById(R.id.videoUrlInput);
        MaterialButton saveButton = dialog.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> {
            try {
                String name = nameInput.getText().toString();
                int sets = Integer.parseInt(setsInput.getText().toString());
                int reps = Integer.parseInt(repsInput.getText().toString());
                int restTime = Integer.parseInt(restTimeInput.getText().toString());
                String instructions = instructionsInput.getText().toString();
                String safetyTips = safetyTipsInput.getText().toString();
                String videoUrl = videoUrlInput.getText().toString();

                Exercise newExercise = new Exercise(name,
                        "Intermediate",
                        sets,
                        reps,
                        restTime,
                        instructions,
                        safetyTips,
                        videoUrl
                );

                exerciseList.add(newExercise);
                exerciseAdapter.setExercises(exerciseList);
                dialog.dismiss();
            } catch (NumberFormatException e) {
                showError("Please enter valid numbers");
            }
        });

        dialog.show();
    }

    private void showError(String message) {
        Snackbar.make(exerciseRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
