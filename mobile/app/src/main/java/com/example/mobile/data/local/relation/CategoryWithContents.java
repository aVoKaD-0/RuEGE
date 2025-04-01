package com.example.mobile.data.local.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mobile.data.local.entity.CategoryEntity;
import com.example.mobile.data.local.entity.ContentEntity;

import java.util.List;

public class CategoryWithContents {
    @Embedded
    public CategoryEntity category;

    @Relation(
        parentColumn = "category_id",
        entityColumn = "parent_id"
    )
    public List<ContentEntity> contents;
} 