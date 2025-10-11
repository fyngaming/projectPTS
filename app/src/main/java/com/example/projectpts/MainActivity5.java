package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity5 extends AppCompatActivity {

    private LinearLayout tasksContainer;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<Catatan> catatanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Inisialisasi SharedPreferences dan Gson
        sharedPreferences = getSharedPreferences("notes_prefs", MODE_PRIVATE);
        gson = new Gson();

        initViews();
        setupBackButton();
        loadCatatanFromStorage();
        setupFooterNavigation();
    }

    private void initViews() {
        tasksContainer = findViewById(R.id.tasksContainer);

        // Setup back button
        ImageButton backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> kembaliKeActivity3());
        }
    }

    private void setupBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                kembaliKeActivity3();
            }
        });
    }

    private void loadCatatanFromStorage() {
        catatanList.clear();
        catatanList = loadCatatanList();

        if (catatanList.isEmpty()) {
            // Jika tidak ada catatan, tampilkan sample data
            loadSampleTasks();
        } else {
            // Tampilkan catatan dari storage
            displayTasks();
        }
    }

    private List<Catatan> loadCatatanList() {
        String catatanJson = sharedPreferences.getString("catatan_list", "");
        if (catatanJson.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Catatan>>(){}.getType();
        List<Catatan> catatanList = gson.fromJson(catatanJson, type);
        return catatanList != null ? catatanList : new ArrayList<>();
    }

    private void saveCatatanList(List<Catatan> catatanList) {
        String catatanJson = gson.toJson(catatanList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("catatan_list", catatanJson);
        editor.apply();
    }

    private void loadSampleTasks() {
        // Data contoh tasks (hanya digunakan jika tidak ada data)
        catatanList.add(new Catatan("Mengerjakan Project Android", "Buat aplikasi manajemen tugas dengan fitur CRUD dan database"));
        catatanList.add(new Catatan("Belajar Pemrograman Kotlin", "Mempelajari dasar-dasar pemrograman Kotlin dan Android Development"));

        // Tampilkan tasks
        displayTasks();
    }

    private void displayTasks() {
        tasksContainer.removeAllViews();

        if (catatanList.isEmpty()) {
            // Tampilkan pesan jika tidak ada catatan
            TextView emptyText = new TextView(this);
            emptyText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            emptyText.setText("Belum ada catatan. Silakan tambah catatan baru!");
            emptyText.setTextColor(0xFF666666);
            emptyText.setTextSize(14);
            emptyText.setPadding(dpToPx(16), dpToPx(32), dpToPx(16), dpToPx(32));
            emptyText.setGravity(android.view.Gravity.CENTER);
            tasksContainer.addView(emptyText);
            return;
        }

        for (int i = 0; i < catatanList.size(); i++) {
            Catatan catatan = catatanList.get(i);
            addTaskItem(catatan, i);
        }
    }

    private void addTaskItem(Catatan catatan, int position) {
        // Create main task item container
        LinearLayout taskItem = new LinearLayout(this);
        LinearLayout.LayoutParams taskParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskParams.setMargins(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4));
        taskItem.setLayoutParams(taskParams);
        taskItem.setOrientation(LinearLayout.HORIZONTAL);
        taskItem.setBackgroundResource(R.drawable.notes_edittext_background);
        taskItem.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        // Checkbox
        ImageView btnCheckbox = new ImageView(this);
        LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(dpToPx(24), dpToPx(24));
        checkboxParams.setMargins(0, 0, dpToPx(12), 0);
        btnCheckbox.setLayoutParams(checkboxParams);

        // Coba gunakan custom drawable, jika tidak ada gunakan system default
        try {
            btnCheckbox.setBackgroundResource(R.drawable.checkbox_circle);
        } catch (Exception e) {
            btnCheckbox.setBackgroundResource(android.R.drawable.btn_default_small);
        }

        // Set initial checkbox state
        boolean isCompleted = catatan.isCompleted();

        // Coba gunakan custom ic_check, jika tidak ada gunakan system icon
        try {
            btnCheckbox.setImageResource(isCompleted ? R.drawable.ic_check : 0);
        } catch (Exception e) {
            btnCheckbox.setImageResource(isCompleted ? android.R.drawable.checkbox_on_background : 0);
        }
        btnCheckbox.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        textContainer.setLayoutParams(textParams);
        textContainer.setOrientation(LinearLayout.VERTICAL);

        // Task Name
        TextView tvTaskName = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvTaskName.setLayoutParams(nameParams);
        tvTaskName.setText(catatan.getJudul());
        tvTaskName.setTextSize(16);
        tvTaskName.setTextColor(isCompleted ? 0xFF888888 : 0xFF2C3E50);
        tvTaskName.setTypeface(null, Typeface.BOLD);
        if (isCompleted) {
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Task Content
        TextView tvTaskContent = new TextView(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.setMargins(0, dpToPx(2), 0, 0);
        tvTaskContent.setLayoutParams(contentParams);
        tvTaskContent.setText(catatan.getIsi());
        tvTaskContent.setTextSize(12);
        tvTaskContent.setTextColor(isCompleted ? 0xFFAAAAAA : 0xFF666666);
        tvTaskContent.setMaxLines(2);
        if (isCompleted) {
            tvTaskContent.setPaintFlags(tvTaskContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Add views to containers
        textContainer.addView(tvTaskName);
        textContainer.addView(tvTaskContent);

        taskItem.addView(btnCheckbox);
        taskItem.addView(textContainer);

        // Add click listener for checkbox
        btnCheckbox.setOnClickListener(v ->
                toggleTaskCompletion(catatan, position, btnCheckbox, tvTaskName, tvTaskContent)
        );

        // Add task item to container
        tasksContainer.addView(taskItem);
    }

    private void toggleTaskCompletion(Catatan catatan, int position, ImageView btnCheckbox, TextView tvTaskName, TextView tvTaskContent) {
        boolean newCompletedState = !catatan.isCompleted();

        // Update status completed
        catatan.setCompleted(newCompletedState);

        // Update SharedPreferences
        saveCatatanList(catatanList);

        // Update UI
        if (newCompletedState) {
            // Mark as completed
            try {
                btnCheckbox.setImageResource(R.drawable.ic_check);
            } catch (Exception e) {
                btnCheckbox.setImageResource(android.R.drawable.checkbox_on_background);
            }
            tvTaskName.setTextColor(0xFF888888);
            tvTaskContent.setTextColor(0xFFAAAAAA);
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvTaskContent.setPaintFlags(tvTaskContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            Toast.makeText(this, "Task completed!", Toast.LENGTH_SHORT).show();
        } else {
            // Mark as not completed
            btnCheckbox.setImageResource(0);
            tvTaskName.setTextColor(0xFF2C3E50);
            tvTaskContent.setTextColor(0xFF666666);
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tvTaskContent.setPaintFlags(tvTaskContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            Toast.makeText(this, "Task reopened!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFooterNavigation() {
        try {
            // Footer Home - Navigate to MainActivity3
            ImageView footerHome = findViewById(R.id.footer_home);
            if (footerHome != null) {
                footerHome.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity5.this, MainActivity3.class);
                    startActivity(intent);
                    finish();
                });
            }

            // Footer Add - Navigate to MainActivity4 (Add Notes)
            ImageView footerAdd = findViewById(R.id.footer_add);
            if (footerAdd != null) {
                footerAdd.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity5.this, MainActivity4.class);
                    startActivity(intent);
                    finish();
                });
            }

            // Footer Notes - Navigate to MainActivity4
            ImageView footerNotes = findViewById(R.id.footer_notes);
            if (footerNotes != null) {
                footerNotes.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity5.this, MainActivity4.class);
                    startActivity(intent);
                    finish();
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error setting up footer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void kembaliKeActivity3() {
        Intent intent = new Intent(MainActivity5.this, MainActivity3.class);
        startActivity(intent);
        finish();
    }

    // Helper method untuk konversi dp ke px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}