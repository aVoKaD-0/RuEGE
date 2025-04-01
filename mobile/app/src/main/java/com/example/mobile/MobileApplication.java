package com.example.mobile;

import android.app.Application;
import android.util.Log;
import com.example.mobile.data.local.AppDatabase;
import dagger.hilt.android.HiltAndroidApp;

/**
 * Кастомный класс Application, аннотированный HiltAndroidApp для поддержки Dagger Hilt.
 * Это корневой класс для внедрения зависимостей в приложении.
 */
@HiltAndroidApp
public class MobileApplication extends Application {

    private static final String TAG = "MobileApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate called - Hilt enabled");
        // Инициализация БД через getInstance больше не нужна здесь,
        // Hilt позаботится об этом при первом запросе БД.
        // database = AppDatabase.getInstance(this);
        // Log.d(TAG, "Database instance created.");
    }
} 