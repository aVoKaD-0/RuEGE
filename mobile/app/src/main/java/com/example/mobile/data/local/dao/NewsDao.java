package com.example.mobile.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.mobile.data.local.entity.NewsEntity;
import java.util.List;
// Используем полные имена для Flow и List в сигнатурах

@Dao // Обязательная аннотация для DAO
public interface NewsDao {

    /**
     * Вставляет список новостей в базу данных.
     * Если новость с таким же newsId уже существует, она будет заменена.
     * Этот метод должен вызываться из корутины (например, через suspend функцию в репозитории).
     *
     * @param newsList Список новостей для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NewsEntity> newsList); // Убираем suspend, используем Java синтаксис

    /**
     * Получает все новости из базы данных, отсортированные по дате публикации (сначала новые).
     * Возвращает Flow, который будет автоматически эмитить новый список при изменениях в таблице.
     *
     * @return Flow со списком всех новостей.
     */
    @Query("SELECT * FROM news ORDER BY publication_date DESC")
    kotlinx.coroutines.flow.Flow<java.util.List<NewsEntity>> getAllNews(); // Используем Java сигнатуру для Flow

    /**
     * Удаляет все новости из таблицы.
     * Этот метод должен вызываться из корутины.
     */
    @Query("DELETE FROM news")
    void deleteAll(); // Убираем suspend

    // Пример для getNewsById на Java:
    /*
    @Query("SELECT * FROM news WHERE news_id = :id")
    kotlinx.coroutines.flow.Flow<NewsEntity> getNewsById(long id);
    */
} 