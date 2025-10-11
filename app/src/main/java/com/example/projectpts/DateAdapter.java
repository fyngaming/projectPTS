package com.example.projectpts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private List<DateItem> dateList;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(DateItem dateItem);
    }

    public DateAdapter(List<DateItem> dateList, OnDateClickListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateItem item = dateList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay;
        View selectionIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            selectionIndicator = itemView.findViewById(R.id.selectionIndicator);
        }

        void bind(DateItem item, OnDateClickListener listener) {
            tvDay.setText(String.valueOf(item.getDay()));

            // Set text color based on state
            if (!item.isCurrentMonth()) {
                tvDay.setTextColor(Color.GRAY);
            } else if (item.isToday()) {
                tvDay.setTextColor(Color.BLUE);
            } else {
                tvDay.setTextColor(Color.parseColor("#2C3E50"));
            }

            // Show selection indicator
            selectionIndicator.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);

            itemView.setOnClickListener(v -> listener.onDateClick(item));
        }
    }
}