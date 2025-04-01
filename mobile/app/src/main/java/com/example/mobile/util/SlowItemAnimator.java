package com.example.mobile.util;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class SlowItemAnimator extends DefaultItemAnimator {

    private static final long DURATION_MS = 500; // Устанавливаем желаемую длительность в миллисекундах

    public SlowItemAnimator() {
        // Устанавливаем одинаковую длительность для всех типов анимаций
        setAddDuration(DURATION_MS);
        setRemoveDuration(DURATION_MS);
        setMoveDuration(DURATION_MS);
        setChangeDuration(DURATION_MS);
    }

    @Override
    public long getAddDuration() {
        return DURATION_MS;
    }

    @Override
    public long getRemoveDuration() {
        return DURATION_MS;
    }

    @Override
    public long getMoveDuration() {
        return DURATION_MS;
    }

    @Override
    public long getChangeDuration() {
        return DURATION_MS;
    }
}