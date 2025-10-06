package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        initViews();
        setupButtonListeners();
        setupBackPressedHandler();
        setupCalendar();
        loadCatatanDariStorage();
        tampilkanCatatan();
    }

    private void initViews() {
        backButton = findViewById(R.id.backButton);
        tasksContainer = findViewById(R.id.tasksContainer);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        daftarCatatan = new ArrayList<>();
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> kembaliKeActivity3());
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
            // Set bulan dan tahun
            tvMonthYear.setText("SEPTEMBER 2025");

            // Highlight tanggal hari ini (contoh: tanggal 17)
            highlightToday(17);

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

        // Highlight tanggal hari ini
        int todayResId = getResources().getIdentifier("date" + today, "id", getPackageName());
        TextView tvToday = findViewById(todayResId);
        if (tvToday != null) {
            tvToday.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tvToday.setBackgroundResource(R.drawable.circle_background);
            tvToday.setTypeface(null, Typeface.BOLD);
        }
    }

    private void loadCatatanDariStorage() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("catatan", MODE_PRIVATE);

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

                            Catatan catatan = new Catatan(judul, isi, timestamp);
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
        LinearLayout taskItem = new LinearLayout(this);
        taskItem.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        taskItem.setOrientation(LinearLayout.VERTICAL);
        taskItem.setBackgroundResource(R.drawable.notes_edittext_background);
        taskItem.setPadding(20, 16, 20, 16);

        LinearLayout.LayoutParams marginParams = (LinearLayout.LayoutParams) taskItem.getLayoutParams();
        marginParams.setMargins(0, 0, 0, 16);
        taskItem.setLayoutParams(marginParams);

        // Header: Judul dan Timestamp
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Judul Task
        TextView tvTaskName = new TextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        tvTaskName.setLayoutParams(titleParams);
        tvTaskName.setText(catatan.getJudul());
        tvTaskName.setTextColor(0xFF2C3E50);
        tvTaskName.setTextSize(16f);
        tvTaskName.setTypeface(null, Typeface.BOLD);
        tvTaskName.setPadding(0, 0, 8, 0);

        // Timestamp
        TextView tvTimestamp = new TextView(this);
        tvTimestamp.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvTimestamp.setText(formatTimestamp(catatan.getTimestamp()));
        tvTimestamp.setTextColor(0xFF888888);
        tvTimestamp.setTextSize(12f);
        tvTimestamp.setTypeface(null, Typeface.NORMAL);

        headerLayout.addView(tvTaskName);
        headerLayout.addView(tvTimestamp);

        // Isi Task
        TextView tvTaskContent = new TextView(this);
        tvTaskContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvTaskContent.setText(catatan.getIsi());
        tvTaskContent.setTextColor(0xFF666666);
        tvTaskContent.setTextSize(14f);
        tvTaskContent.setPadding(0, 8, 0, 8);
        tvTaskContent.setMaxLines(3);
        tvTaskContent.setEllipsize(android.text.TextUtils.TruncateAt.END);

        // Footer: Nomor Task
        TextView tvTaskNumber = new TextView(this);
        tvTaskNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tvTaskNumber.setText("(" + nomor + "." + total + ")");
        tvTaskNumber.setTextColor(0xFF888888);
        tvTaskNumber.setTextSize(12f);
        tvTaskNumber.setGravity(android.view.Gravity.END);

        // Tambahkan semua view ke taskItem
        taskItem.addView(headerLayout);
        taskItem.addView(tvTaskContent);
        taskItem.addView(tvTaskNumber);

        tasksContainer.addView(taskItem);
    }

    private String formatTimestamp(String timestamp) {
        try {
            long timeMillis = Long.parseLong(timestamp);
            Date date = new Date(timeMillis);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return dateFormat.format(date);

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

        public Catatan(String judul, String isi, String timestamp) {
            this.judul = judul;
            this.isi = isi;
            this.timestamp = timestamp;
        }

        public String getJudul() { return judul; }
        public String getIsi() { return isi; }
        public String getTimestamp() { return timestamp; }
    }
}