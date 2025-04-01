package com.example.mobile.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
    tableName = "progress",
    foreignKeys = {
        @ForeignKey(
            entity = UserEntity.class,
            parentColumns = "user_id",
            childColumns = "user_id",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = ContentEntity.class,
            parentColumns = "content_id",
            childColumns = "content_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(name = "index_progress_user_id", value = "user_id"),
        @Index(name = "index_progress_content_id", value = "content_id"),
        @Index(name = "index_progress_user_content", value = {"user_id", "content_id"}, unique = true)
    }
)
public class ProgressEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "progress_id")
    private long progressId;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "content_id")
    private String contentId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "percentage")
    private int percentage;

    @ColumnInfo(name = "last_accessed")
    private long lastAccessed;

    @ColumnInfo(name = "completed")
    private boolean completed;

    // Конструктор без параметров для Room
    public ProgressEntity() {
    }

    @Ignore
    public ProgressEntity(long userId, String contentId, String title, int percentage, 
                         long lastAccessed, boolean completed) {
        this.userId = userId;
        this.contentId = contentId;
        this.title = title;
        this.percentage = percentage;
        this.lastAccessed = lastAccessed;
        this.completed = completed;
    }

    // Геттеры и Сеттеры
    public long getProgressId() {
        return progressId;
    }

    public void setProgressId(long progressId) {
        this.progressId = progressId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
} 