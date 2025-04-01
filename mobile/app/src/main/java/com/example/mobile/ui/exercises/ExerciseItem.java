package com.example.mobile.ui.exercises;

public class ExerciseItem {
    private String title;
    private String description;
    private String difficulty;
    private int numberOfQuestions;

    public ExerciseItem(String title, String description, String difficulty, int numberOfQuestions) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }
} 