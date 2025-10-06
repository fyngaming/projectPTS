package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity5 extends AppCompatActivity {

    private ImageButton backButton;
    private LinearLayout tasksContainer;
    private List<Catatan> daftarCatatan;
    private TextView tvMonthYear;
    private SharedPreferences sharedPreferences;

    // Footer views
    private ImageView footerHome, footerAdd, footerNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        initViews();
        setupButtonListeners();
        setupBackPressedHandler();
        setupFooterNavigation();
        setupCalendar();
        loadCatatanDariStorage();
        tampilkanCatatan();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        tasksContainer = findViewById(R.id.tasksContainer);
        tvMonthYear = findViewById(R.id.tvMonthYear);

        // Footer initialization
        footerHome = findViewById(R.id.footer_home);
        footerAdd = findViewById(R.id.footer_add);
        footerNotes = findViewById(R.id.footer_notes);

        daftarCatatan = new ArrayList<>();
        sharedPreferences = getSharedPreferences("catatan", MODE_PRIVATE);
    }

    private void setupButtonListeners() {
        // Back button - kembali ke MainActivity3
        backButton.setOnClickListener(v -> kembaliKeActivity3());
    }

    private void setupFooterNavigation() {
        try {
            // Setup Footer Home
            if (footerHome != null) {
                footerHome.setOnClickListener(v -> {
                    Toast.makeText(MainActivity5.this, "Home Clicked", Toast.LENGTH_SHORT).show();
                    // Navigate to MainActivity3 (Home)
                    Intent intent = new Intent(MainActivity5.this, MainActivity3.class);
                    startActivity(intent);
                    finish();
                });
            }

            // Setup Footer Add
            if (footerAdd != null) {
                footerAdd.setOnClickListener(v -> {
                    Toast.makeText(MainActivity5.this, "Add Clicked", Toast.LENGTH_SHORT).show();
                    // Navigate to MainActivity4 (Add Notes)
                    Intent intent = new Intent(MainActivity5.this, MainActivity4.class);
                    startActivity(intent);
                    finish();
                });
            }

            // Setup Footer Notes
            if (footerNotes != null) {
                footerNotes.setOnClickListener(v -> {
                    Toast.makeText(MainActivity5.this, "Notes Clicked", Toast.LENGTH_SHORT).show();
                    // Sudah di MainActivity5 (Notes), tidak perlu navigasi
                    // Bisa tambahkan logic refresh jika diperlukan
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error setting up footer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                kembaliKeActivity3();
            }
        });
    }

    private void setupCalendar() {
        try {
            // Set bulan dan tahun berdasarkan tanggal hari ini
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            tvMonthYear.setText(monthFormat.format(calendar.getTime()).toUpperCase());

            // Highlight tanggal hari ini
            int today = calendar.get(Calendar.DAY_OF_MONTH);
            highlightToday(today);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void highlightToday(int today) {
        // Reset semua tanggal ke warna normal
        for (int i = 13; i <= 19; i++) {
            int resId = getResources().getIdentifier("date" + i, "id", getPackageName());
            TextView tvDate = findViewById(resId);
            if (tvDate != null) {
                tvDate.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                tvDate.setBackgroundResource(android.R.color.transparent);
                tvDate.setTypeface(null, Typeface.NORMAL);
            }
        }

        // Highlight tanggal hari ini jika ada dalam range 13-19
        if (today >= 13 && today <= 19) {
            int todayResId = getResources().getIdentifier("date" + today, "id", getPackageName());
            TextView tvToday = findViewById(todayResId);
            if (tvToday != null) {
                tvToday.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                tvToday.setBackgroundResource(R.drawable.circle_background);
                tvToday.setTypeface(null, Typeface.BOLD);
            }
        }
    }

    private void loadCatatanDariStorage() {
        try {
            // Ambil semua catatan dari SharedPreferences
            for (String key : sharedPreferences.getAll().keySet()) {
                if (key.startsWith("catatan_")) {
                    String catatanData = sharedPreferences.getString(key, "");
                    if (!catatanData.isEmpty()) {
                        String[] parts = catatanData.split("\\|\\|\\|");
                        if (parts.length >= 2) {
                            String judul = parts[0];
                            String isi = parts[1];
                            String timestamp = parts.length > 2 ? parts[2] : String.valueOf(System.currentTimeMillis());

                            Catatan catatan = new Catatan(judul, isi, timestamp, key);
                            daftarCatatan.add(catatan);
                        }
                    }
                }
            }

            // Urutkan catatan berdasarkan timestamp (terbaru pertama)
            daftarCatatan.sort((c1, c2) -> Long.compare(
                    Long.parseLong(c2.getTimestamp()),
                    Long.parseLong(c1.getTimestamp())
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tampilkanCatatan() {
        tasksContainer.removeAllViews();

        if (daftarCatatan.isEmpty()) {
            TextView tvNoTasks = new TextView(this);
            tvNoTasks.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tvNoTasks.setText("Belum ada catatan. Silakan buat catatan baru di halaman Notes.");
            tvNoTasks.setTextColor(0xFF666666);
            tvNoTasks.setTextSize(16f);
            tvNoTasks.setPadding(16, 32, 16, 32);
            tvNoTasks.setGravity(android.view.Gravity.CENTER);
            tasksContainer.addView(tvNoTasks);
            return;
        }

        for (int i = 0; i < daftarCatatan.size(); i++) {
            Catatan catatan = daftarCatatan.get(i);
            addTaskItem(catatan, i + 1, daftarCatatan.size());
        }
    }

    private void addTaskItem(Catatan catatan, int nomor, int total) {
        // Cek status completed dari SharedPreferences
        boolean isCompleted = sharedPreferences.getBoolean(catatan.getKey() + "_completed", false);

        // Main container
        LinearLayout taskItem = new LinearLayout(this);
        taskItem.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        taskItem.setOrientation(LinearLayout.HORIZONTAL);
        taskItem.setBackgroundResource(R.drawable.notes_edittext_background);
        taskItem.setPadding(20, 16, 20, 16);

        LinearLayout.LayoutParams marginParams = (LinearLayout.LayoutParams) taskItem.getLayoutParams();
        marginParams.setMargins(0, 0, 0, 16);
        taskItem.setLayoutParams(marginParams);

        // Checkbox circle
        ImageButton btnCheckbox = new ImageButton(this);
        LinearLayout.LayoutParams checkboxParams = new LinearLayout.LayoutParams(
                dpToPx(40),
                dpToPx(40)
        );
        checkboxParams.setMargins(0, 0, dpToPx(12), 0);
        btnCheckbox.setLayoutParams(checkboxParams);
        btnCheckbox.setBackgroundResource(R.drawable.checkbox_circle);
        btnCheckbox.setImageResource(isCompleted ? R.drawable.ic_check : 0);
        btnCheckbox.setScaleType(android.widget.ImageView.ScaleType.CENTER_INSIDE);

        // Animasi untuk checkbox
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation);

        // Text container
        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        textContainer.setOrientation(LinearLayout.VERTICAL);

        // Task Name (Judul)
        TextView tvTaskName = new TextView(this);
        tvTaskName.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvTaskName.setText(catatan.getJudul());
        tvTaskName.setTextColor(isCompleted ? 0xFF888888 : 0xFF2C3E50);
        tvTaskName.setTextSize(16f);
        tvTaskName.setTypeface(null, Typeface.BOLD);
        tvTaskName.setPadding(0, 0, 0, 4);

        // Strikethrough jika completed
        if (isCompleted) {
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Task Content (Isi)
        TextView tvTaskContent = new TextView(this);
        tvTaskContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvTaskContent.setText(catatan.getIsi());
        tvTaskContent.setTextColor(isCompleted ? 0xFFAAAAAA : 0xFF666666);
        tvTaskContent.setTextSize(14f);
        tvTaskContent.setPadding(0, 0, 0, 4);
        tvTaskContent.setMaxLines(2);
        tvTaskContent.setEllipsize(android.text.TextUtils.TruncateAt.END);

        if (isCompleted) {
            tvTaskContent.setPaintFlags(tvTaskContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Timestamp
        TextView tvTimestamp = new TextView(this);
        tvTimestamp.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvTimestamp.setText(formatTimestamp(catatan.getTimestamp()));
        tvTimestamp.setTextColor(0xFF888888);
        tvTimestamp.setTextSize(12f);
        tvTimestamp.setTypeface(null, Typeface.NORMAL);

        // Task Number
        TextView tvTaskNumber = new TextView(this);
        tvTaskNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvTaskNumber.setText("(" + nomor + "." + total + ")");
        tvTaskNumber.setTextColor(0xFF888888);
        tvTaskNumber.setTextSize(12f);

        // Add views to text container
        textContainer.addView(tvTaskName);
        textContainer.addView(tvTaskContent);
        textContainer.addView(tvTimestamp);

        // Add views to main container
        taskItem.addView(btnCheckbox);
        taskItem.addView(textContainer);
        taskItem.addView(tvTaskNumber);

        // Checkbox click listener
        btnCheckbox.setOnClickListener(v -> {
            toggleTaskCompletion(catatan, taskItem, btnCheckbox, tvTaskName, tvTaskContent);
            btnCheckbox.startAnimation(scaleAnimation);
        });

        tasksContainer.addView(taskItem);
    }

    private void toggleTaskCompletion(Catatan catatan, LinearLayout taskItem,
                                      ImageButton btnCheckbox, TextView tvTaskName, TextView tvTaskContent) {
        boolean isCurrentlyCompleted = sharedPreferences.getBoolean(catatan.getKey() + "_completed", false);
        boolean newCompletedState = !isCurrentlyCompleted;

        // Update SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(catatan.getKey() + "_completed", newCompletedState);
        editor.apply();

        // Update UI
        if (newCompletedState) {
            // Mark as completed
            btnCheckbox.setImageResource(R.drawable.ic_check);
            tvTaskName.setTextColor(0xFF888888);
            tvTaskContent.setTextColor(0xFFAAAAAA);
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvTaskContent.setPaintFlags(tvTaskContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // Mark as not completed
            btnCheckbox.setImageResource(0);
            tvTaskName.setTextColor(0xFF2C3E50);
            tvTaskContent.setTextColor(0xFF666666);
            tvTaskName.setPaintFlags(tvTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tvTaskContent.setPaintFlags(tvTaskContent.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    private String formatTimestamp(String timestamp) {
        try {
            long timeMillis = Long.parseLong(timestamp);
            Date date = new Date(timeMillis);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm", Locale.getDefault());
            return "Created: " + dateFormat.format(date);

        } catch (Exception e) {
            return "Tanggal tidak tersedia";
        }
    }

    private void kembaliKeActivity3() {
        Intent intent = new Intent(MainActivity5.this, MainActivity3.class);
        startActivity(intent);
        finish();
    }

    // Model class untuk Catatan
    private static class Catatan {
        private String judul;
        private String isi;
        private String timestamp;
        private String key;

        public Catatan(String judul, String isi, String timestamp, String key) {
            this.judul = judul;
            this.isi = isi;
            this.timestamp = timestamp;
            this.key = key;
        }

        public String getJudul() { return judul; }
        public String getIsi() { return isi; }
        public String getTimestamp() { return timestamp; }
        public String getKey() { return key; }
    }
}