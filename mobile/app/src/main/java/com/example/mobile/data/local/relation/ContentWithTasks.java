package com.example.mobile.data.local.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.mobile.data.local.entity.ContentEntity;
import com.example.mobile.data.local.entity.TaskEntity;

import java.util.List;

public class ContentWithTasks {
    @Embedded
    public ContentEntity content;

    @Relation(
        parentColumn = "content_id",
        entityColumn = "content_id"
    )
    public List<TaskEntity> tasks;
} 