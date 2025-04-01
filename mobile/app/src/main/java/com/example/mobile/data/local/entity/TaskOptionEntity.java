package com.example.mobile.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
    tableName = "task_options",
    foreignKeys = {
        @ForeignKey(
            entity = TaskEntity.class,
            parentColumns = "task_id",
            childColumns = "task_id",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(name = "index_task_options_task_id", value = "task_id")
    }
)
public class TaskOptionEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "option_id")
    private long optionId;

    @ColumnInfo(name = "task_id")
    private String taskId;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "is_correct")
    private boolean isCorrect;

    @ColumnInfo(name = "explanation")
    private String explanation;

    @ColumnInfo(name = "order_position")
    private int orderPosition;

    // Конструктор без параметров для Room
    public TaskOptionEntity() {
    }

    @Ignore
    public TaskOptionEntity(String taskId, String text, boolean isCorrect, String explanation, int orderPosition) {
        this.taskId = taskId;
        this.text = text;
        this.isCorrect = isCorrect;
        this.explanation = explanation;
        this.orderPosition = orderPosition;
    }

    // Геттеры и Сеттеры
    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(int orderPosition) {
        this.orderPosition = orderPosition;
    }
} 