package com.example.mobile.util;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.mobile.model.NewsItem; // Убедитесь, что путь к модели верный

import java.util.List;
import java.util.Objects;

public class NewsDiffCallback extends DiffUtil.Callback {

    private final List<NewsItem> oldList;
    private final List<NewsItem> newList;

    public NewsDiffCallback(List<NewsItem> oldList, List<NewsItem> newList) {
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
        // Если у NewsItem нет ID, нужно придумать надежный способ идентификации
        // (например, комбинация заголовка и даты, или использовать URL, если он уникален).
        NewsItem oldItem = oldList.get(oldItemPosition);
        NewsItem newItem = newList.get(newItemPosition);
        // TODO: Заменить getTitle() на getId() или другой уникальный идентификатор
        // Возможно, стоит использовать комбинацию полей:
        // return Objects.equals(oldItem.getTitle(), newItem.getTitle()) && Objects.equals(oldItem.getDate(), newItem.getDate());
        return Objects.equals(oldItem.getTitle(), newItem.getTitle()); // Временное решение
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Сравниваем все поля, которые влияют на отображение.
        NewsItem oldItem = oldList.get(oldItemPosition);
        NewsItem newItem = newList.get(newItemPosition);
        // TODO: Добавить сравнение других полей (например, getSummary(), getDate()), если они отображаются
        return Objects.equals(oldItem.getTitle(), newItem.getTitle())
                && Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl());
                // && Objects.equals(oldItem.getSummary(), newItem.getSummary()) // Раскомментируйте, если getSummary() существует и используется
                // && Objects.equals(oldItem.getDate(), newItem.getDate());      // Раскомментируйте, если getDate() существует и используется
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}