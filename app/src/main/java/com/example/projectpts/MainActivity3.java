package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity3 extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final String TAG = "MainActivity3";

    // SharedPreferences keys - HARUS SAMA di semua Activity
    private static final String PREFS_NAME = "TaskPrefs";
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Initialize SharedPreferences dan Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        setupButtons();
        setupFooterNavigation();

        // Debug: cek tasks yang tersimpan
        debugCheckTasks();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupButtons() {
        try {
            // Setup ADD TASK button - NAVIGASI KE MainActivity6
            LinearLayout btnAddTask = findViewById(R.id.btnAddTask);
            if (btnAddTask != null) {
                btnAddTask.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "ADD TASK Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity3.this, MainActivity6.class);
                    startActivity(intent);
                });
            }

            // Setup VIEW TASK button - NAVIGASI KE MainActivity5
            LinearLayout btnViewTask = findViewById(R.id.btnViewTask);
            if (btnViewTask != null) {
                btnViewTask.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "VIEW TASK Clicked", Toast.LENGTH_SHORT).show();

                    // Cek apakah ada tasks sebelum navigasi
                    List<Task> allTasks = loadTasksFromStorage();
                    if (allTasks.isEmpty()) {
                        Toast.makeText(MainActivity3.this, "No tasks available. Please add a task first.", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(MainActivity3.this, MainActivity5.class);
                        startActivity(intent);
                    }
                });
            }

            // Setup NOTES button - NAVIGASI KE MainActivity8
            LinearLayout btnNotes = findViewById(R.id.btnNotes);
            if (btnNotes != null) {
                btnNotes.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "NOTES Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity3.this, MainActivity8.class);
                    startActivity(intent);
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error setting up buttons: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error setting up buttons", e);
        }
    }

    private void setupFooterNavigation() {
        try {
            // Setup Footer Home - Refresh current page
            ImageView footerHome = findViewById(R.id.footer_home);
            if (footerHome != null) {
                footerHome.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "Home Clicked", Toast.LENGTH_SHORT).show();
                    // Refresh data
                    debugCheckTasks();
                });
            }

            // Setup Footer Add - NAVIGASI KE MainActivity6 (Add Task)
            ImageView footerAdd = findViewById(R.id.footer_add);
            if (footerAdd != null) {
                footerAdd.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "Add Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity3.this, MainActivity6.class);
                    startActivity(intent);
                });
            }

            // Setup Footer Notes - NAVIGASI KE MainActivity7 (INBOX/REMINDER) - YANG DIUBAH
            ImageView footerNotes = findViewById(R.id.footer_notes);
            if (footerNotes != null) {
                footerNotes.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "Reminder Inbox Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity3.this, MainActivity7.class);
                    startActivity(intent);
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error setting up footer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error setting up footer", e);
        }
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

    private void debugCheckTasks() {
        List<Task> tasks = loadTasksFromStorage();
        Log.d(TAG, "Total tasks in storage: " + tasks.size());

        // Tampilkan di log detail tasks
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Log.d(TAG, "Task " + i + ": " + task.getTaskName() + " - " + task.getDate());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data ketika kembali ke activity ini
        debugCheckTasks();
    }
}