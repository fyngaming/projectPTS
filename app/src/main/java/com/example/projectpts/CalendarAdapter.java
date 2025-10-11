package com.example.projectpts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<MainActivity6.CalendarDay> calendarDays;
    private String selectedDate;
    private OnDateClickListener onDateClickListener;

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    public CalendarAdapter(List<MainActivity6.CalendarDay> calendarDays, String selectedDate, OnDateClickListener listener) {
        this.calendarDays = calendarDays;
        this.selectedDate = selectedDate;
        this.onDateClickListener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        MainActivity6.CalendarDay day = calendarDays.get(position);

        holder.tvDayName.setText(day.getDayName());
        holder.tvDateNumber.setText(day.getDateNumber());

        if (day.isSelected()) {
            holder.itemView.setBackgroundResource(R.drawable.calendar_day_selected);
            holder.tvDayName.setTextColor(Color.WHITE);
            holder.tvDateNumber.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.calendar_day_background);
            holder.tvDayName.setTextColor(Color.parseColor("#666666"));
            holder.tvDateNumber.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onDateClickListener != null) {
                onDateClickListener.onDateClick(day.getFullDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName;
        TextView tvDateNumber;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDateNumber = itemView.findViewById(R.id.tvDateNumber);
        }
    }
}