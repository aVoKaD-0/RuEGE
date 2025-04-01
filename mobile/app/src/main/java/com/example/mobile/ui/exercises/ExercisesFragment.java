package com.example.mobile.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.databinding.FragmentExercisesBinding;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding binding;
    private ExercisesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Настройка RecyclerView
        RecyclerView recyclerView = binding.recyclerViewExercises;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        // Создаем и устанавливаем адаптер
        adapter = new ExercisesAdapter(getExerciseItems());
        recyclerView.setAdapter(adapter);
    }

    // Временный метод для создания тестовых данных
    private List<ExerciseItem> getExerciseItems() {
        List<ExerciseItem> items = new ArrayList<>();
        
        items.add(new ExerciseItem("Задание 1. Ударение", 
                "Расставьте ударения в словах", "Легкая", 10));
        items.add(new ExerciseItem("Задание 2. Лексические нормы", 
                "Исправьте лексические ошибки", "Средняя", 15));
        items.add(new ExerciseItem("Задание 3. Морфологические нормы", 
                "Исправьте морфологические ошибки", "Средняя", 12));
        items.add(new ExerciseItem("Задание 4. Синтаксические нормы", 
                "Установите соответствие между предложениями и ошибками", "Сложная", 20));
        items.add(new ExerciseItem("Задание 5. Орфографические нормы", 
                "Вставьте пропущенные буквы", "Сложная", 25));
        items.add(new ExerciseItem("Задание 6. Пунктуационные нормы", 
                "Расставьте знаки препинания", "Сложная", 25));
        items.add(new ExerciseItem("Задание 7. Синтаксический анализ", 
                "Анализ предложения", "Средняя", 15));
        items.add(new ExerciseItem("Задание 8. Типы речи", 
                "Определите тип речи", "Легкая", 10));
        
        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 