package com.example.mobile.di;

import android.content.Context;
import androidx.room.Room;
import com.example.mobile.data.local.AppDatabase;
import com.example.mobile.data.local.dao.NewsDao;
import com.example.mobile.data.repository.NewsRepository;

import javax.inject.Singleton; // Важно: используем javax.inject.Singleton

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent; // Компонент для application scope

@Module // Указывает, что это модуль Hilt
@InstallIn(SingletonComponent.class) // Указывает, что зависимости будут жить пока живо приложение (application scope)
public class DatabaseModule {

    // Метод, который говорит Hilt, как создать AppDatabase
    @Provides // Указывает, что этот метод предоставляет зависимость
    @Singleton // Указывает, что должен быть только один экземпляр AppDatabase
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        // Используем тот же код для создания БД, что был в getInstance
        // Но теперь его вызовет Hilt, когда потребуется AppDatabase
        return Room.databaseBuilder(
                context, // Hilt автоматически предоставит ApplicationContext
                AppDatabase.class,
                "mobile_database.db" // Имя файла БД
        )
        // .fallbackToDestructiveMigration() // Можно раскомментировать, если не хотим пока писать миграции
        .build();
    }

    // Метод, который говорит Hilt, как создать NewsDao
    @Provides
    // @Singleton // DAO обычно не делают Singleton, Hilt создаст его, когда нужно
    public NewsDao provideNewsDao(AppDatabase appDatabase) {
        // Просто получаем DAO из экземпляра AppDatabase, который Hilt создаст сам
        return appDatabase.newsDao();
    }

    // Метод, который говорит Hilt, как создать NewsRepository
    @Provides
    @Singleton // Репозиторий часто делают Singleton
    public NewsRepository provideNewsRepository(NewsDao newsDao) {
        // Hilt автоматически предоставит NewsDao, созданный методом выше
        return new NewsRepository(newsDao);
    }
} 