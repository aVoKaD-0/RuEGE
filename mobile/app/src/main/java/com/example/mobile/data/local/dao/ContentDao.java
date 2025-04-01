package com.example.mobile.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import com.example.mobile.data.local.entity.ContentEntity;
import com.example.mobile.data.local.relation.ContentWithTasks;

import java.util.List;

@Dao
public interface ContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContentEntity content);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ContentEntity> contents);

    @Update
    void update(ContentEntity content);

    @Delete
    void delete(ContentEntity content);

    @Query("SELECT * FROM contents WHERE content_id = :contentId")
    LiveData<ContentEntity> getContentById(String contentId);

    @Query("SELECT * FROM contents WHERE parent_id = :parentId ORDER BY order_position ASC")
    LiveData<List<ContentEntity>> getContentsByParentId(String parentId);

    @Query("SELECT * FROM contents WHERE type = :type ORDER BY order_position ASC")
    LiveData<List<ContentEntity>> getContentsByType(String type);

    @Query("SELECT * FROM contents WHERE is_downloaded = 1 ORDER BY order_position ASC")
    LiveData<List<ContentEntity>> getDownloadedContents();

    @Query("SELECT * FROM contents WHERE is_new = 1 ORDER BY order_position ASC")
    LiveData<List<ContentEntity>> getNewContents();

    @Query("UPDATE contents SET is_downloaded = :isDownloaded WHERE content_id = :contentId")
    void updateDownloadStatus(String contentId, boolean isDownloaded);

    @Query("UPDATE contents SET is_new = :isNew WHERE content_id = :contentId")
    void updateNewStatus(String contentId, boolean isNew);

    @Transaction
    @Query("SELECT * FROM contents WHERE content_id = :contentId")
    LiveData<ContentWithTasks> getContentWithTasks(String contentId);

    @Transaction
    @Query("SELECT * FROM contents WHERE type = 'task' ORDER BY order_position ASC")
    LiveData<List<ContentWithTasks>> getAllTaskContentsWithTasks();
} 