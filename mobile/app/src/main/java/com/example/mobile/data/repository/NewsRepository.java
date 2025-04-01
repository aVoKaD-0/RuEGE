package com.example.mobile.data.repository;

import android.util.Log; // Для логирования

import com.example.mobile.data.local.dao.NewsDao;
import com.example.mobile.data.local.entity.NewsEntity;

import java.util.ArrayList; // Для создания моковых данных
import java.util.List;
import kotlinx.coroutines.flow.Flow;

/**
 * Репозиторий для управления данными новостей.
 * Скрывает источники данных (БД, сеть) от остального приложения.
 */
public class NewsRepository {

    private static final String TAG = "NewsRepository";
    private final NewsDao newsDao;

    // Конструктор, принимающий DAO
    public NewsRepository(NewsDao newsDao) {
        this.newsDao = newsDao;
    }

    /**
     * Возвращает поток (Flow) со списком всех новостей из локальной базы данных.
     * Поток будет автоматически обновляться при изменениях в таблице новостей.
     */
    public Flow<List<NewsEntity>> getAllNewsStream() {
        Log.d(TAG, "Getting all news stream from DAO");
        return newsDao.getAllNews();
    }

    /**
     * Метод для обновления новостей (например, из сети).
     * В данный момент просто вставляет несколько моковых новостей для демонстрации.
     * В реальном приложении здесь будет логика сетевого запроса.
     * Должен вызываться из корутины.
     */
    public /*suspend*/ void refreshNews() { // Убираем suspend, так как insertAll его не требует в сигнатуре DAO
        try {
            Log.d(TAG, "Refreshing news data (inserting mock data)...");
            // Создаем моковые данные
            List<NewsEntity> mockNews = new ArrayList<>();
            // Используем System.currentTimeMillis() для получения текущей даты в виде timestamp
            long now = System.currentTimeMillis();
            // R.drawable нам недоступен здесь, используем строки-заглушки для image URL
            mockNews.add(new NewsEntity("Локальное Обновление!", now - 1000*60*60*24, // Сутки назад
                    "Данные загружены из локальной БД.", "placeholder_image_1"));
            mockNews.add(new NewsEntity("Room Работает!", now - 1000*60*30, // 30 мин назад
                    "База данных успешно инициализирована и используется.", "placeholder_image_2"));
             mockNews.add(new NewsEntity("Скоро Сеть!", now, // Сейчас
                    "Следующим шагом будет загрузка из сети.", "placeholder_image_3"));

            // Сначала очистим старые новости (опционально, зависит от логики)
            // newsDao.deleteAll();
            // Вставляем новые данные
            newsDao.insertAll(mockNews);
            Log.d(TAG, "Mock news inserted successfully.");
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing news", e);
            // Здесь можно обработать ошибку вставки в БД
        }
    }

    /**
     * Очищает таблицу новостей в базе данных.
     * Должен вызываться из корутины.
     */
    public /*suspend*/ void clearAllNews() { // Убираем suspend
        try {
            Log.d(TAG, "Clearing all news from database...");
            newsDao.deleteAll();
            Log.d(TAG, "News table cleared.");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing news table", e);
        }
    }
} 