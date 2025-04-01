package com.example.mobile.ui.exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExerciseViewHolder> {

    private List<ExerciseItem> exerciseItems;

    public ExercisesAdapter(List<ExerciseItem> exerciseItems) {
        this.exerciseItems = exerciseItems;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        ExerciseItem item = exerciseItems.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
        holder.difficultyTextView.setText("Сложность: " + item.getDifficulty());

        holder.buttonStartExercise.setOnClickListener(v -> {
            // Временно показываем Toast, в будущем добавим переход к экрану с заданием
            Toast.makeText(v.getContext(), "Начинаем: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return exerciseItems.size();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView difficultyTextView;
        MaterialButton buttonStartExercise;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.exerciseTitle);
            descriptionTextView = itemView.findViewById(R.id.exerciseDescription);
            difficultyTextView = itemView.findViewById(R.id.exerciseDifficulty);
            buttonStartExercise = itemView.findViewById(R.id.btnStartExercise);
        }
    }
} 