package com.example.mobile.ui.theory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.databinding.FragmentTheoryBinding;

import java.util.ArrayList;
import java.util.List;

public class TheoryFragment extends Fragment {

    private static final String TAG = "TheoryFragment";
    private FragmentTheoryBinding binding;
    private TheoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            binding = FragmentTheoryBinding.inflate(inflater, container, false);
            return binding.getRoot();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании view: " + e.getMessage(), e);
            // Возвращаем простой view в случае ошибки
            return inflater.inflate(R.layout.fragment_theory_fallback, container, false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        try {
            // Настройка RecyclerView
            RecyclerView recyclerView = binding.recyclerViewTheory;
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            
            // Создаем и устанавливаем адаптер
            adapter = new TheoryAdapter(getTheoryItems());
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при настройке RecyclerView: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Произошла ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
        }
    }

    // Временный метод для создания тестовых данных
    private List<TheoryItem> getTheoryItems() {
        List<TheoryItem> items = new ArrayList<>();
        
        items.add(new TheoryItem("Фонетика", "Раздел науки о языке, изучающий звуки речи"));
        items.add(new TheoryItem("Орфоэпия", "Раздел науки о языке, изучающий нормы произношения"));
        items.add(new TheoryItem("Лексикология", "Раздел науки о языке, изучающий словарный состав"));
        items.add(new TheoryItem("Морфемика", "Раздел науки о языке, изучающий морфемы"));
        items.add(new TheoryItem("Словообразование", "Раздел науки о языке, изучающий образование слов"));
        items.add(new TheoryItem("Морфология", "Раздел науки о языке, изучающий части речи"));
        items.add(new TheoryItem("Синтаксис", "Раздел науки о языке, изучающий предложения и словосочетания"));
        items.add(new TheoryItem("Орфография", "Раздел науки о языке, изучающий правила написания слов"));
        items.add(new TheoryItem("Пунктуация", "Раздел науки о языке, изучающий знаки препинания"));
        
        return items;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 