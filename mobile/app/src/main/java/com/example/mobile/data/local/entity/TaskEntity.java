package com.example.mobile.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
    tableName = "tasks",
    foreignKeys = {
        @ForeignKey(
            entity = ContentEntity.class,
            parentColumns = "content_id",
            childColumns = "content_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(name = "index_tasks_content_id", value = "content_id")
    }
)
public class TaskEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "task_id")
    private String taskId;

    @ColumnInfo(name = "content_id")
    private String contentId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "difficulty")
    private int difficulty; // 1-5, где 5 - самое сложное

    @ColumnInfo(name = "max_points")
    private int maxPoints;

    @ColumnInfo(name = "task_type")
    private String taskType; // test, coding, essay

    @ColumnInfo(name = "time_limit")
    private int timeLimit; // в секундах, 0 - без ограничения

    // Конструктор без параметров для Room
    public TaskEntity() {
    }

    @Ignore
    public TaskEntity(String taskId, String contentId, String title, String description,
                     int difficulty, int maxPoints, String taskType, int timeLimit) {
        this.taskId = taskId;
        this.contentId = contentId;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.maxPoints = maxPoints;
        this.taskType = taskType;
        this.timeLimit = timeLimit;
    }

    // Геттеры и Сеттеры
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
} 