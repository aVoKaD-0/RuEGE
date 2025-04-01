package com.example.mobile;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Импортируем Toolbar

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Получаем Toolbar из layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Устанавливаем Toolbar как ActionBar

        // Включаем кнопку "Назад"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Получаем данные из Intent
        String title = getIntent().getStringExtra("content_title");
        String description = getIntent().getStringExtra("content_description");
        // String contentId = getIntent().getStringExtra("content_id"); // Можно использовать для загрузки доп. данных

        // Устанавливаем заголовок в Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title != null ? title : "Детали");
        }

        // Находим TextView для описания
        TextView descriptionTextView = findViewById(R.id.detail_description);
        if (descriptionTextView != null) {
            descriptionTextView.setText(description != null ? description : "Описание отсутствует.");
        }

        // Здесь можно добавить загрузку и отображение более сложного контента,
        // используя contentId или другие переданные данные.
    }

    // Обработка нажатия кнопки "Назад" в Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        // Используем OnBackPressedDispatcher для обработки нажатия "Назад"
        getOnBackPressedDispatcher().onBackPressed(); 
        return true;
    }
} 