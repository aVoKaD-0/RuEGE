package com.example.mobile.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "news") // Указываем имя таблицы
public class NewsEntity {

    @PrimaryKey(autoGenerate = true) // Первичный ключ с автогенерацией
    @ColumnInfo(name = "news_id") // Имя колонки в БД
    private long newsId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "publication_date") // Храним дату как Long (timestamp)
    private long date;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_url") // Ссылка на изображение
    private String imageUrl;

    // Конструктор для Room (может быть пустым или с полями)
    // Room также может использовать конструктор со всеми полями, если он есть.
    // Для простоты оставим его без аргументов, но добавим сеттеры.
    public NewsEntity() {
    }

    // Геттеры и Сеттеры (необходимы для Room, если поля приватные)
    public long getNewsId() {
        return newsId;
    }

    public void setNewsId(long newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Ignore // Добавляем @Ignore, чтобы Room не использовал этот конструктор
    public NewsEntity(String title, long date, String description, String imageUrl) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
    }
} 