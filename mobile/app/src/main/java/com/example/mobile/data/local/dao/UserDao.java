package com.example.mobile.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import com.example.mobile.data.local.entity.UserEntity;
import com.example.mobile.data.local.relation.UserWithProgress;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Delete
    void delete(UserEntity user);

    @Query("SELECT * FROM users WHERE user_id = :userId")
    kotlinx.coroutines.flow.Flow<UserEntity> getUserById(long userId);

    @Query("SELECT * FROM users ORDER BY username ASC")
    kotlinx.coroutines.flow.Flow<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    kotlinx.coroutines.flow.Flow<UserEntity> getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    kotlinx.coroutines.flow.Flow<UserEntity> getUserByUsername(String username);

    @Query("UPDATE users SET last_login = :timestamp WHERE user_id = :userId")
    void updateLastLogin(long userId, long timestamp);

    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :userId")
    kotlinx.coroutines.flow.Flow<UserWithProgress> getUserWithProgress(long userId);
} 