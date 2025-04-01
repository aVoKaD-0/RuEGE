package com.example.mobile.ui.theory;

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

public class TheoryAdapter extends RecyclerView.Adapter<TheoryAdapter.TheoryViewHolder> {

    private List<TheoryItem> theoryItems;

    public TheoryAdapter(List<TheoryItem> theoryItems) {
        this.theoryItems = theoryItems;
    }

    @NonNull
    @Override
    public TheoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theory, parent, false);
        return new TheoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheoryViewHolder holder, int position) {
        TheoryItem item = theoryItems.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());

        holder.buttonViewTheory.setOnClickListener(v -> {
            // Временно показываем Toast, в будущем добавим переход к экрану с теорией
            Toast.makeText(v.getContext(), "Открываем: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return theoryItems.size();
    }

    static class TheoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        MaterialButton buttonViewTheory;

        public TheoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.theoryTitle);
            descriptionTextView = itemView.findViewById(R.id.theoryDescription);
            buttonViewTheory = itemView.findViewById(R.id.btnViewTheory);
        }
    }
} 