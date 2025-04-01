package com.example.mobile.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.mobile.data.local.entity.ProgressEntity;

import java.util.List;

@Dao
public interface ProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProgressEntity progress);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ProgressEntity> progressList);

    @Update
    void update(ProgressEntity progress);

    @Delete
    void delete(ProgressEntity progress);

    @Query("SELECT * FROM progress WHERE progress_id = :progressId")
    LiveData<ProgressEntity> getProgressById(long progressId);

    @Query("SELECT * FROM progress WHERE user_id = :userId")
    LiveData<List<ProgressEntity>> getProgressByUserId(long userId);

    @Query("SELECT * FROM progress WHERE content_id = :contentId")
    LiveData<List<ProgressEntity>> getProgressByContentId(String contentId);

    @Query("SELECT * FROM progress WHERE user_id = :userId AND content_id = :contentId")
    LiveData<ProgressEntity> getUserProgressForContent(long userId, String contentId);

    @Query("UPDATE progress SET percentage = :percentage, last_accessed = :timestamp WHERE user_id = :userId AND content_id = :contentId")
    void updateProgress(long userId, String contentId, int percentage, long timestamp);

    @Query("UPDATE progress SET completed = 1, percentage = 100, last_accessed = :timestamp WHERE user_id = :userId AND content_id = :contentId")
    void markAsCompleted(long userId, String contentId, long timestamp);

    @Query("SELECT AVG(percentage) FROM progress WHERE user_id = :userId")
    LiveData<Integer> getAverageProgressForUser(long userId);

    @Query("SELECT COUNT(*) FROM progress WHERE user_id = :userId AND completed = 1")
    LiveData<Integer> getCompletedContentCountForUser(long userId);
} 