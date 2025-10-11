package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity7 extends AppCompatActivity {

    private RecyclerView recyclerViewInbox;
    private InboxAdapter inboxAdapter;
    private TextView tvEmptyInbox;
    private List<Task> taskList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final String PREFS_NAME = "TaskPrefs";
    private static final String TASKS_KEY = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);

        // Initialize SharedPreferences and Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();
        taskList = new ArrayList<>();

        initViews();
        setupBackButton();
        loadTasksFromStorage();
        setupRecyclerView();
        checkIfEmpty();
    }

    private void initViews() {
        recyclerViewInbox = findViewById(R.id.recyclerViewInbox);
        tvEmptyInbox = findViewById(R.id.tvEmptyInbox);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> kembaliKeActivity3());
    }

    private void setupBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                kembaliKeActivity3();
            }
        });
    }

    private void setupRecyclerView() {
        inboxAdapter = new InboxAdapter();
        recyclerViewInbox.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInbox.setAdapter(inboxAdapter);
        inboxAdapter.setTasks(taskList);
    }

    private void loadTasksFromStorage() {
        String tasksJson = sharedPreferences.getString(TASKS_KEY, "");
        if (!tasksJson.isEmpty()) {
            Type type = new TypeToken<List<Task>>(){}.getType();
            List<Task> savedTasks = gson.fromJson(tasksJson, type);
            if (savedTasks != null) {
                taskList.clear();
                taskList.addAll(savedTasks);

                // Debug log
                android.util.Log.d("INBOX_LOAD", "Loaded " + taskList.size() + " tasks from storage");
                for (Task task : taskList) {
                    android.util.Log.d("INBOX_TASK", "Task: " + task.getTaskName() +
                            " | Date: " + task.getDate() +
                            " | Time: " + task.getStartTime());
                }
            }
        } else {
            android.util.Log.d("INBOX_LOAD", "No tasks found in storage");
        }
    }

    private void checkIfEmpty() {
        if (taskList.isEmpty()) {
            tvEmptyInbox.setVisibility(View.VISIBLE);
            recyclerViewInbox.setVisibility(View.GONE);
        } else {
            tvEmptyInbox.setVisibility(View.GONE);
            recyclerViewInbox.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload tasks when returning to this activity
        loadTasksFromStorage();
        if (inboxAdapter != null) {
            inboxAdapter.setTasks(taskList);
            checkIfEmpty();
        }
    }

    private void kembaliKeActivity3() {
        Intent intent = new Intent(MainActivity7.this, MainActivity3.class);
        startActivity(intent);
        finish();
    }
}