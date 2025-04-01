package com.example.mobile.util;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.mobile.model.ContentItem; // Убедитесь, что путь к модели верный

import java.util.List;
import java.util.Objects;

public class ContentDiffCallback extends DiffUtil.Callback {

    private final List<ContentItem> oldList;
    private final List<ContentItem> newList;

    public ContentDiffCallback(List<ContentItem> oldList, List<ContentItem> newList) {
        this.oldList = oldList == null ? List.of() : oldList;
        this.newList = newList == null ? List.of() : newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Сравниваем по уникальному идентификатору.
        // Предполагаем, что у ContentItem есть getId().
        ContentItem oldItem = oldList.get(oldItemPosition);
        ContentItem newItem = newList.get(newItemPosition);
        // TODO: Убедитесь, что getId() возвращает уникальный и стабильный ID
        return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Сравниваем все поля, которые влияют на отображение.
        ContentItem oldItem = oldList.get(oldItemPosition);
        ContentItem newItem = newList.get(newItemPosition);
        // TODO: Добавьте сравнение других полей (icon, description, isNew и т.д.), если они влияют на UI
        return Objects.equals(oldItem.getTitle(), newItem.getTitle())
                && Objects.equals(oldItem.getDescription(), newItem.getDescription()) // Добавляем сравнение описания
                && Objects.equals(oldItem.getType(), newItem.getType()) // Добавляем сравнение типа
                && oldItem.isNew() == newItem.isNew(); // Сравниваем флаг "Новый"
                // && Objects.equals(oldItem.getIconUrl(), newItem.getIconUrl()); // Если есть иконка
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}