package com.example.mobile.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(
    tableName = "contents",
    foreignKeys = {
        @ForeignKey(
            entity = CategoryEntity.class,
            parentColumns = "category_id",
            childColumns = "parent_id",
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    },
    indices = {
        @Index(name = "index_contents_parent_id", value = "parent_id")
    }
)
public class ContentEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "content_id")
    private String contentId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "type")
    private String type; // theory, task, cheatsheet, variant
    
    @ColumnInfo(name = "parent_id")
    private String parentId; // ID родительской категории или родительского контента
    
    @ColumnInfo(name = "is_downloaded")
    private boolean isDownloaded;
    
    @ColumnInfo(name = "is_new")
    private boolean isNew;
    
    @ColumnInfo(name = "order_position")
    private int orderPosition;
    
    @ColumnInfo(name = "content_url")
    private String contentUrl;

    // Конструктор без параметров для Room
    public ContentEntity() {
    }

    @Ignore
    public ContentEntity(String contentId, String title, String description, String type, String parentId, 
                        boolean isDownloaded, boolean isNew, int orderPosition, String contentUrl) {
        this.contentId = contentId;
        this.title = title;
        this.description = description;
        this.type = type;
        this.parentId = parentId;
        this.isDownloaded = isDownloaded;
        this.isNew = isNew;
        this.orderPosition = orderPosition;
        this.contentUrl = contentUrl;
    }

    // Геттеры и Сеттеры
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getOrderPosition() {
        return orderPosition;
    }

    public void setOrderPosition(int orderPosition) {
        this.orderPosition = orderPosition;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
} 