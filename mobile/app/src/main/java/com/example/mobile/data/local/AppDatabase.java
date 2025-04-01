package com.example.mobile.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mobile.data.local.dao.CategoryDao;
import com.example.mobile.data.local.dao.ContentDao;
import com.example.mobile.data.local.dao.NewsDao;
import com.example.mobile.data.local.dao.ProgressDao;
import com.example.mobile.data.local.dao.TaskDao;
import com.example.mobile.data.local.dao.UserDao;
import com.example.mobile.data.local.dao.UserTaskAttemptDao;

import com.example.mobile.data.local.entity.CategoryEntity;
import com.example.mobile.data.local.entity.ContentEntity;
import com.example.mobile.data.local.entity.NewsEntity;
import com.example.mobile.data.local.entity.ProgressEntity;
import com.example.mobile.data.local.entity.TaskEntity;
import com.example.mobile.data.local.entity.TaskOptionEntity;
import com.example.mobile.data.local.entity.UserEntity;
import com.example.mobile.data.local.entity.UserTaskAttemptEntity;
import com.example.mobile.data.local.migration.Migration1to2;

// Аннотация @Database определяет класс как базу данных Room
@Database(
    entities = {
        NewsEntity.class,
        UserEntity.class,
        CategoryEntity.class,
        ContentEntity.class,
        ProgressEntity.class,
        TaskEntity.class,
        TaskOptionEntity.class,
        UserTaskAttemptEntity.class
    },
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Абстрактные методы для получения DAO
    public abstract NewsDao newsDao();
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ContentDao contentDao();
    public abstract ProgressDao progressDao();
    public abstract TaskDao taskDao();
    public abstract UserTaskAttemptDao userTaskAttemptDao();

    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "mobile_database.db";

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                        )
                        .fallbackToDestructiveMigration() // Пересоздаём БД при обновлении версии
                        .addMigrations(new Migration1to2()) // Добавляем миграцию с версии 1 на версию 2
                        .build();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Метод для полной очистки базы данных и пересоздания ее с нуля.
     * Используйте только в случае критической необходимости.
     * @param context Контекст приложения
     */
    public static void clearAndRebuildDatabase(final Context context) {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
        
        // Удаляем файл базы данных полностью
        context.deleteDatabase(DATABASE_NAME);
        
        // Получаем новый экземпляр
        getInstance(context);
    }
} 