package com.example.mobile.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import com.example.mobile.data.local.entity.TaskEntity;
import com.example.mobile.data.local.entity.TaskOptionEntity;
import com.example.mobile.data.local.relation.TaskWithOptions;

import java.util.List;

@Dao
public interface TaskDao {

    // Методы для TaskEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskEntity task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TaskEntity> tasks);

    @Update
    void update(TaskEntity task);

    @Delete
    void delete(TaskEntity task);

    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    kotlinx.coroutines.flow.Flow<TaskEntity> getTaskById(String taskId);

    @Query("SELECT * FROM tasks WHERE content_id = :contentId")
    kotlinx.coroutines.flow.Flow<List<TaskEntity>> getTasksByContentId(String contentId);

    @Query("SELECT * FROM tasks WHERE difficulty <= :maxDifficulty")
    kotlinx.coroutines.flow.Flow<List<TaskEntity>> getTasksByMaxDifficulty(int maxDifficulty);

    @Query("SELECT * FROM tasks WHERE task_type = :taskType")
    kotlinx.coroutines.flow.Flow<List<TaskEntity>> getTasksByType(String taskType);

    // Методы для TaskOptionEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOption(TaskOptionEntity option);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllOptions(List<TaskOptionEntity> options);

    @Update
    void updateOption(TaskOptionEntity option);

    @Delete
    void deleteOption(TaskOptionEntity option);

    @Query("SELECT * FROM task_options WHERE option_id = :optionId")
    kotlinx.coroutines.flow.Flow<TaskOptionEntity> getOptionById(long optionId);

    @Query("SELECT * FROM task_options WHERE task_id = :taskId ORDER BY order_position ASC")
    kotlinx.coroutines.flow.Flow<List<TaskOptionEntity>> getOptionsByTaskId(String taskId);

    @Query("SELECT * FROM task_options WHERE task_id = :taskId AND is_correct = 1")
    kotlinx.coroutines.flow.Flow<List<TaskOptionEntity>> getCorrectOptionsByTaskId(String taskId);

    // Транзакционные запросы для получения задания с вариантами ответов
    @Transaction
    @Query("SELECT * FROM tasks WHERE task_id = :taskId")
    kotlinx.coroutines.flow.Flow<TaskWithOptions> getTaskWithOptions(String taskId);

    @Transaction
    @Query("SELECT * FROM tasks WHERE content_id = :contentId")
    kotlinx.coroutines.flow.Flow<List<TaskWithOptions>> getTasksWithOptionsByContentId(String contentId);
} 