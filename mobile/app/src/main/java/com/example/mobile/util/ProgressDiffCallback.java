package com.example.mobile.util;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.mobile.model.ProgressItem; // Убедитесь, что путь к модели верный

import java.util.List;
import java.util.Objects; // Импортируем Objects для сравнения

public class ProgressDiffCallback extends DiffUtil.Callback {

    private final List<ProgressItem> oldList;
    private final List<ProgressItem> newList;

    public ProgressDiffCallback(List<ProgressItem> oldList, List<ProgressItem> newList) {
        this.oldList = oldList == null ? List.of() : oldList; // Защита от null
        this.newList = newList == null ? List.of() : newList; // Защита от null
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
        // Сравниваем по уникальному идентификатору, если он есть.
        // Если нет, сравниваем по заголовку, но это менее надежно.
        // ЗАМЕНИТЕ getTitle() на getId(), если у ProgressItem есть уникальный ID.
        ProgressItem oldItem = oldList.get(oldItemPosition);
        ProgressItem newItem = newList.get(newItemPosition);
        // TODO: Заменить getTitle() на getId() или другой уникальный идентификатор
        return Objects.equals(oldItem.getTitle(), newItem.getTitle());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Сравниваем все поля, которые влияют на отображение.
        ProgressItem oldItem = oldList.get(oldItemPosition);
        ProgressItem newItem = newList.get(newItemPosition);
        // TODO: Добавить сравнение других полей, если они есть и влияют на UI
        return Objects.equals(oldItem.getTitle(), newItem.getTitle())
                && oldItem.getPercentage() == newItem.getPercentage();
        // Используем Objects.equals для безопасного сравнения заголовков (на случай null)
    }

    // Опционально: для более эффективного обновления при изменении только части элемента
    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Можно реализовать, если нужно обновлять только конкретные View внутри ViewHolder,
        // а не весь элемент целиком. Сейчас возвращаем null (полное обновление элемента).
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}