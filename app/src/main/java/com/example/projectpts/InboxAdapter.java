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
import java.util.Calendar;
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

            // Set date group - PERBAIKAN: gunakan task date bukan timestamp
            String dateGroup = getDateGroup(task.getDate());
            tvDateGroup.setText(dateGroup);

            // Set message
            String message = getMessageForTask(task.getTaskName());
            tvMessage.setText(message);

            // Set task details
            String details = String.format("%s (%s - %s)",
                    task.getTaskName(),
                    task.getStartTime(),
                    task.getEndTime());
            tvTaskDetails.setText(details);

            // Set color indicator
            try {
                colorIndicator.setBackgroundColor(task.getColor());
            } catch (Exception e) {
                colorIndicator.setBackgroundColor(Color.parseColor("#4CAF50"));
            }
        }

        private String getDateGroup(String taskDate) {
            try {
                if (taskDate == null || taskDate.isEmpty()) {
                    return "Unknown";
                }

                // Parse task date string to determine if it's today, yesterday, etc.
                SimpleDateFormat taskDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                Date taskDateObj = taskDateFormat.parse(taskDate);

                if (taskDateObj == null) {
                    return taskDate; // Return original date if parsing fails
                }

                Calendar taskCal = Calendar.getInstance();
                taskCal.setTime(taskDateObj);

                Calendar today = Calendar.getInstance();
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_YEAR, -1);

                if (isSameDay(taskCal, today)) {
                    return "Today";
                } else if (isSameDay(taskCal, yesterday)) {
                    return "Yesterday";
                } else {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    return displayFormat.format(taskDateObj);
                }
            } catch (Exception e) {
                return taskDate != null ? taskDate : "Unknown";
            }
        }

        private boolean isSameDay(Calendar cal1, Calendar cal2) {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                    cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
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
    }
}