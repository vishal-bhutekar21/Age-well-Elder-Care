package com.chaitany.agewell;


import java.util.List;

public class Workout {
    private Long id;
    private String title;
    private List<Exercise> exercises;
    private double progress;

    // Constructors
    public Workout() {}

    public Workout(String title, List<Exercise> exercises) {
        this.title = title;
        this.exercises = exercises;
        this.progress = 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }
}