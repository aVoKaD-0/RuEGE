package com.example.mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.R;
import com.example.mobile.model.ProgressItem;
import com.example.mobile.util.ProgressDiffCallback;

import java.util.ArrayList;
import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder> {

    private List<ProgressItem> progressItems;
    private final OnProgressClickListener onProgressClickListener;
    
    public interface OnProgressClickListener {
        void onProgressClick(ProgressItem progressItem);
    }

    public ProgressAdapter(List<ProgressItem> progressItems, OnProgressClickListener listener) {
        this.progressItems = new ArrayList<>(progressItems == null ? List.of() : progressItems);
        this.onProgressClickListener = listener;
    }

    @NonNull
    @Override
    public ProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
        return new ProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressViewHolder holder, int position) {
        ProgressItem item = progressItems.get(position);
        holder.titleView.setText(item.getTitle());
        holder.progressBar.setProgress(item.getPercentage());
        holder.percentView.setText(String.format("%d%%", item.getPercentage()));

        holder.itemView.setOnClickListener(v -> onProgressClickListener.onProgressClick(item));
    }

    @Override
    public int getItemCount() {
        return progressItems.size();
    }
    
    public void submitList(List<ProgressItem> newItems) {
        List<ProgressItem> newProgressList = new ArrayList<>(newItems == null ? List.of() : newItems);
        ProgressDiffCallback diffCallback = new ProgressDiffCallback(this.progressItems, newProgressList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        
        this.progressItems.clear();
        this.progressItems.addAll(newProgressList);
        diffResult.dispatchUpdatesTo(this);
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        ProgressBar progressBar;
        TextView percentView;

        public ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.progress_title);
            progressBar = itemView.findViewById(R.id.progress_bar);
            percentView = itemView.findViewById(R.id.progress_percent);
        }
    }
} 