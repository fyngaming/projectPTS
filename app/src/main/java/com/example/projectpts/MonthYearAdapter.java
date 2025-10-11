package com.example.projectpts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthYearAdapter extends RecyclerView.Adapter<MonthYearAdapter.ViewHolder> {
    private List<MonthYearItem> itemList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, MonthYearItem item);
    }

    public MonthYearAdapter(List<MonthYearItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MonthYearItem item = itemList.get(position);
        holder.bind(item, listener, position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }

        void bind(MonthYearItem item, OnItemClickListener listener, int position) {
            textView.setText(item.getLabel());

            // Set background based on selection
            if (item.isSelected()) {
                textView.setBackgroundColor(Color.LTGRAY);
            } else {
                textView.setBackgroundColor(Color.TRANSPARENT);
            }

            itemView.setOnClickListener(v -> {
                // Deselect all items
                for (MonthYearItem i : itemList) {
                    i.setSelected(false);
                }
                // Select current item
                item.setSelected(true);
                notifyDataSetChanged();

                listener.onItemClick(position, item);
            });
        }
    }
}