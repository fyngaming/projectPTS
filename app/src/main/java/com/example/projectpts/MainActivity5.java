package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity5 extends AppCompatActivity {

    private LinearLayout tasksContainer;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final String TAG = "MainActivity5";

    private static final String PREFS_NAME = "TaskPrefs";
    private static final String TASKS_KEY = "tasks";
    private static final String COMPLETED_TASKS_KEY = "completed_tasks";

    private Set<String> completedTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Initialize SharedPreferences dan Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        // Load completed tasks
        completedTasks = sharedPreferences.getStringSet(COMPLETED_TASKS_KEY, new HashSet<>());

        initViews();
        setupBackButton();
        setupFooterNavigation();
        loadAndDisplayTasks();
    }

    private void initViews() {
        tasksContainer = findViewById(R.id.tasksContainer);
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            kembaliKeMainActivity3();
        });
    }

    private void setupFooterNavigation() {
        // Logo Home di footer - kembali ke MainActivity3
        findViewById(R.id.footer_home).setOnClickListener(v -> {
            kembaliKeMainActivity3();
        });

        // Logo Add di footer - pergi ke MainActivity6 (Add Task)
        findViewById(R.id.footer_add).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity5.this, MainActivity6.class);
            startActivity(intent);
        });

        // Logo Notes di footer - pergi ke MainActivity7 (INBOX/REMINDER) - YANG DIUBAH
        findViewById(R.id.footer_notes).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity5.this, MainActivity7.class);
            startActivity(intent);
        });
    }

    private void kembaliKeMainActivity3() {
        Intent intent = new Intent(MainActivity5.this, MainActivity3.class);
        startActivity(intent);
        finish(); // Tutup activity ini agar tidak menumpuk di back stack
    }

    private void loadAndDisplayTasks() {
        List<Task> allTasks = loadTasksFromStorage();

        Log.d(TAG, "Loading tasks for display. Total: " + allTasks.size());

        if (allTasks.isEmpty()) {
            showEmptyState();
        } else {
            displayTasks(allTasks);
        }
    }

    private void displayTasks(List<Task> tasks) {
        tasksContainer.removeAllViews();

        for (Task task : tasks) {
            addTaskToView(task);
        }

        String displayMessage = getString(R.string.displaying_tasks, tasks.size());
        Toast.makeText(this, displayMessage, Toast.LENGTH_SHORT).show();
    }

    private void addTaskToView(Task task) {
        // Inflate custom task item layout
        View taskItem = LayoutInflater.from(this).inflate(R.layout.task_item_layout, tasksContainer, false);

        // Get views
        RelativeLayout checkCircle = taskItem.findViewById(R.id.checkCircle);
        ImageView checkMark = taskItem.findViewById(R.id.checkMark);
        TextView tvTaskName = taskItem.findViewById(R.id.tvTaskName);
        TextView tvTaskDate = taskItem.findViewById(R.id.tvTaskDate);
        TextView tvTaskTime = taskItem.findViewById(R.id.tvTaskTime);
        View colorIndicator = taskItem.findViewById(R.id.colorIndicator);
        LinearLayout taskItemLayout = taskItem.findViewById(R.id.taskItemLayout);

        // Set task data
        tvTaskName.setText(task.getTaskName());

        String dateText = getString(R.string.task_date, task.getDate());
        tvTaskDate.setText(dateText);

        String timeText = getString(R.string.task_time, task.getStartTime(), task.getEndTime());
        tvTaskTime.setText(timeText);

        // Set task color
        colorIndicator.setBackgroundColor(task.getColor());

        // Check if task is completed
        boolean isCompleted = completedTasks.contains(getTaskIdentifier(task));
        updateTaskAppearance(isCompleted, tvTaskName, tvTaskDate, tvTaskTime, checkCircle, checkMark);

        // Set click listener for checkbox
        checkCircle.setOnClickListener(v -> {
            boolean currentlyCompleted = completedTasks.contains(getTaskIdentifier(task));
            toggleTaskCompletion(task, !currentlyCompleted, tvTaskName, tvTaskDate, tvTaskTime, checkCircle, checkMark);
        });

        // Set click listener for entire task item
        taskItemLayout.setOnClickListener(v -> {
            boolean currentlyCompleted = completedTasks.contains(getTaskIdentifier(task));
            toggleTaskCompletion(task, !currentlyCompleted, tvTaskName, tvTaskDate, tvTaskTime, checkCircle, checkMark);
        });

        // Add task item to container
        tasksContainer.addView(taskItem);
    }

    private void toggleTaskCompletion(Task task, boolean completed,
                                      TextView tvTaskName, TextView tvTaskDate, TextView tvTaskTime,
                                      RelativeLayout checkCircle, ImageView checkMark) {

        String taskIdentifier = getTaskIdentifier(task);

        if (completed) {
            // Mark as completed
            completedTasks.add(taskIdentifier);
            playCheckmarkAnimation(checkCircle, checkMark);
            applyTextAnimation(tvTaskName); // Animasi teks saat dicentang
        } else {
            // Mark as incomplete
            completedTasks.remove(taskIdentifier);
            playUncheckAnimation(checkCircle, checkMark);
            removeTextAnimation(tvTaskName); // Hapus animasi teks saat unchecked
        }

        // Save completed tasks
        saveCompletedTasks();

        // Update appearance
        updateTaskAppearance(completed, tvTaskName, tvTaskDate, tvTaskTime, checkCircle, checkMark);
    }

    private void updateTaskAppearance(boolean isCompleted,
                                      TextView tvTaskName, TextView tvTaskDate, TextView tvTaskTime,
                                      RelativeLayout checkCircle, ImageView checkMark) {

        if (isCompleted) {
            // Completed state
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvTaskName.setTextColor(Color.parseColor("#888888"));
            tvTaskDate.setTextColor(Color.parseColor("#AAAAAA"));
            tvTaskTime.setTextColor(Color.parseColor("#AAAAAA"));

            checkCircle.setBackgroundResource(R.drawable.check_circle_filled);
            checkMark.setVisibility(View.VISIBLE);

            // Add fade animation
            Animation fadeAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            tvTaskName.startAnimation(fadeAnimation);

        } else {
            // Incomplete state
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tvTaskName.setTextColor(Color.parseColor("#2C3E50"));
            tvTaskDate.setTextColor(Color.parseColor("#666666"));
            tvTaskTime.setTextColor(Color.parseColor("#666666"));

            checkCircle.setBackgroundResource(R.drawable.check_circle_background);
            checkMark.setVisibility(View.INVISIBLE);
        }
    }

    private void playCheckmarkAnimation(RelativeLayout checkCircle, ImageView checkMark) {
        // Scale animation for circle
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.check_scale);
        checkCircle.startAnimation(scaleAnimation);

        // Fade in animation for check mark
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeInAnimation.setStartOffset(100);
        checkMark.startAnimation(fadeInAnimation);

        // Bounce animation
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);
        checkMark.startAnimation(bounceAnimation);
    }

    private void playUncheckAnimation(RelativeLayout checkCircle, ImageView checkMark) {
        // Scale down animation
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.uncheck_scale);
        checkCircle.startAnimation(scaleAnimation);

        // Fade out animation for check mark
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        checkMark.startAnimation(fadeOutAnimation);
    }

    private void applyTextAnimation(TextView textView) {
        // Animasi fade dan scale untuk teks yang dicentang
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_completed_animation);
        textView.startAnimation(textAnimation);
    }

    private void removeTextAnimation(TextView textView) {
        // Hapus animasi saat unchecked
        textView.clearAnimation();
    }

    private String getTaskIdentifier(Task task) {
        return task.getTaskName() + "|" + task.getDate() + "|" + task.getStartTime() + "|" + task.getEndTime();
    }

    private void saveCompletedTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(COMPLETED_TASKS_KEY, completedTasks);
        editor.apply();
    }

    private void showEmptyState() {
        tasksContainer.removeAllViews();

        TextView tvEmpty = new TextView(this);
        tvEmpty.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvEmpty.setText(getString(R.string.no_tasks_available));
        tvEmpty.setTextColor(Color.GRAY);
        tvEmpty.setTextSize(16);
        tvEmpty.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tvEmpty.setPadding(0, dpToPx(25), 0, dpToPx(25));

        tasksContainer.addView(tvEmpty);

        Toast.makeText(this, getString(R.string.no_tasks_found), Toast.LENGTH_SHORT).show();
    }

    private List<Task> loadTasksFromStorage() {
        String tasksJson = sharedPreferences.getString(TASKS_KEY, "");
        if (tasksJson.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<Task>>(){}.getType();
            List<Task> taskList = gson.fromJson(tasksJson, type);
            return taskList != null ? taskList : new ArrayList<>();
        } catch (Exception e) {
            Log.e(TAG, "Error loading tasks from storage", e);
            return new ArrayList<>();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data ketika kembali ke activity ini
        loadAndDisplayTasks();
    }

    // Helper method untuk konversi dp ke px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}