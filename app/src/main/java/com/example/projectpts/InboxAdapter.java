package com.example.projectpts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.TaskViewHolder> {

    private List<Task> taskList;

    public InboxAdapter() {
        this.taskList = new ArrayList<>();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inbox, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addTask(Task task) {
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.add(task);
        notifyItemInserted(taskList.size() - 1);
    }

    public void clearTasks() {
        if (taskList != null) {
            taskList.clear();
            notifyDataSetChanged();
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateGroup;
        private TextView tvMessage;
        private TextView tvTaskDetails;
        private View colorIndicator;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateGroup = itemView.findViewById(R.id.tvDateGroup);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTaskDetails = itemView.findViewById(R.id.tvTaskDetails);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
        }

        public void bind(Task task) {
            if (task == null) return;

            // Set date group
            String dateGroup = getDateGroup(task.getTimestamp());
            tvDateGroup.setText(dateGroup);

            // Set message
            String message = getMessageForTask(task.getTaskName());
            tvMessage.setText(message);

            // Set task details
            String details = String.format("_%s (%s, %s)_",
                    task.getTaskName(),
                    formatDateForDisplay(task.getDate()),
                    task.getStartTime());
            tvTaskDetails.setText(details);

            // Set color indicator
            try {
                colorIndicator.setBackgroundColor(task.getColor());
            } catch (Exception e) {
                colorIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
            }
        }

        private String getDateGroup(long timestamp) {
            try {
                long now = System.currentTimeMillis();
                long oneDay = 24 * 60 * 60 * 1000;
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                if (format.format(new Date(now)).equals(format.format(new Date(timestamp)))) {
                    return "Today";
                } else if (format.format(new Date(now - oneDay)).equals(format.format(new Date(timestamp)))) {
                    return "Yesterday";
                } else {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    return displayFormat.format(new Date(timestamp));
                }
            } catch (Exception e) {
                return "Unknown";
            }
        }

        private String getMessageForTask(String taskName) {
            if (taskName == null) return "Don't forget your schedule!! 🌟🎵";

            String[] messages = {
                    "Don't forget your schedule!! 🌟🎵",
                    "Don't Forget Your Agenda...!!",
                    "Did You Forget Me??",
                    "Remember your plan! 📅",
                    "Your task is waiting! ⏰"
            };
            int index = Math.abs(taskName.hashCode()) % messages.length;
            return messages[index];
        }

        private String formatDateForDisplay(String date) {
            if (date == null) return "Unknown";

            try {
                if (date.contains("March")) {
                    return date.replace("March ", "").replace(", 2025", "/03");
                }
                return date;
            } catch (Exception e) {
                return date;
            }
        }
    }
}