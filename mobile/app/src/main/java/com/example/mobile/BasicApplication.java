package com.example.mobile;

import android.app.Application;
import android.util.Log;

/**
 * Простой класс Application для тестирования запуска
 */
public class BasicApplication extends Application {

    private static final String TAG = "BasicApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Basic Application onCreate called");
    }
} 