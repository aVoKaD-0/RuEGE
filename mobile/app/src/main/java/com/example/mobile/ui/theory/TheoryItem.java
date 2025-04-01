package com.example.mobile.ui.theory;

public class TheoryItem {
    private String title;
    private String description;
    private String content;

    public TheoryItem(String title, String description) {
        this.title = title;
        this.description = description;
        this.content = "";
    }

    public TheoryItem(String title, String description, String content) {
        this.title = title;
        this.description = description;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 