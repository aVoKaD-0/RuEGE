package com.example.mobile.util;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.example.mobile.model.NavigationItem; // Убедитесь, что путь к модели верный

import java.util.List;
import java.util.Objects;

public class NavigationDiffCallback extends DiffUtil.Callback {

    private final List<NavigationItem> oldList;
    private final List<NavigationItem> newList;

    public NavigationDiffCallback(List<NavigationItem> oldList, List<NavigationItem> newList) {
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
        // Если у NavigationItem нет ID, сравниваем по заголовку или ID ресурса иконки.
        NavigationItem oldItem = oldList.get(oldItemPosition);
        NavigationItem newItem = newList.get(newItemPosition);
        // TODO: Убедитесь, что getTitle() или getIconResId() достаточно уникальны
        // Если есть getId(), используйте его: return Objects.equals(oldItem.getId(), newItem.getId());
        return Objects.equals(oldItem.getTitle(), newItem.getTitle()); // Или oldItem.getIconResId() == newItem.getIconResId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Сравниваем все поля, которые влияют на отображение.
        NavigationItem oldItem = oldList.get(oldItemPosition);
        NavigationItem newItem = newList.get(newItemPosition);
        return Objects.equals(oldItem.getTitle(), newItem.getTitle())
                && oldItem.getIconResId() == newItem.getIconResId();
        // Добавьте сравнение других полей, если они есть и влияют на UI (например, цвет текста/фона)
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Обычно не требуется для простых навигационных элементов
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}