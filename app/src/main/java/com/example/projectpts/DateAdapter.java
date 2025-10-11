package com.example.projectpts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<Calendar> dates;
    private String selectedDate;
    private OnDateClickListener listener;

    public interface OnDateClickListener {
        void onDateClick(String date);
    }

    public DateAdapter(List<Calendar> dates, String selectedDate, OnDateClickListener listener) {
        this.dates = dates;
        this.selectedDate = selectedDate;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Calendar date = dates.get(position);
        holder.bind(date, selectedDate, listener);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDay, tvDate;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(Calendar date, String selectedDate, OnDateClickListener listener) {
            SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.getDefault());
            SimpleDateFormat fullDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

            String day = dayFormat.format(date.getTime());
            String dateNum = dateFormat.format(date.getTime());
            String fullDate = fullDateFormat.format(date.getTime());

            tvDay.setText(day);
            tvDate.setText(dateNum);

            // Highlight selected date
            if (fullDate.equals(selectedDate)) {
                itemView.setBackgroundColor(Color.parseColor("#E3F2FD"));
                tvDate.setTextColor(Color.parseColor("#1976D2"));
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
                tvDate.setTextColor(Color.BLACK);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDateClick(fullDate);
                }
            });
        }
    }
}