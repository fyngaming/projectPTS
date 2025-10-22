package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity6 extends AppCompatActivity {

    private EditText etTaskName, etStartTime, etEndTime;
    private Button btnSaveTask;
    private ImageButton backButton, btnPrevMonth, btnNextMonth;
    private TextView tvCurrentMonthYear, tvSelectedDate;
    private LinearLayout colorContainer;
    private RecyclerView recyclerViewCalendar;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    private Calendar currentCalendar;
    private String selectedDate;
    private int selectedColor = Color.parseColor("#FF6B6B");

    private CalendarAdapter calendarAdapter;
    private List<MainActivity6.CalendarDay> calendarDays;

    private static final String PREFS_NAME = "TaskPrefs";
    private static final String TASKS_KEY = "tasks";

    // Array warna untuk color picker
    private final String[] colorCodes = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        // Initialize SharedPreferences dan Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        // Setup calendar dengan tanggal saat ini
        currentCalendar = Calendar.getInstance();
        selectedDate = getFormattedDate(currentCalendar);

        // Inisialisasi selected color dengan warna pertama
        selectedColor = Color.parseColor(colorCodes[0]);

        initViews();
        setupCalendar();
        setupColorPicker();
        setupSaveButton();
        setupBackButton();
        setupBackPressedHandler();
    }

    private void initViews() {
        // Text inputs
        etTaskName = findViewById(R.id.etTaskName);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);

        // Buttons
        btnSaveTask = findViewById(R.id.btnSaveTask);
        backButton = findViewById(R.id.backButton);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);

        // Calendar views
        tvCurrentMonthYear = findViewById(R.id.tvCurrentMonthYear);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        recyclerViewCalendar = findViewById(R.id.recyclerViewCalendar);
        colorContainer = findViewById(R.id.colorContainer);
    }

    private void setupCalendar() {
        // Set current month year
        updateMonthYearDisplay();

        // Setup RecyclerView untuk calendar
        setupRecyclerViewCalendar();

        // Setup month navigation buttons
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateMonthYearDisplay();
            refreshCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateMonthYearDisplay();
            refreshCalendar();
        });

        // Update selected date display
        updateSelectedDateDisplay();
    }

    private void setupRecyclerViewCalendar() {
        // Buat list tanggal untuk bulan ini
        calendarDays = generateCalendarDays();

        // Setup adapter untuk RecyclerView
        calendarAdapter = new CalendarAdapter(calendarDays, selectedDate, new CalendarAdapter.OnDateClickListener() {
            @Override
            public void onDateClick(String date) {
                // Update selected date dan refresh tampilan
                selectedDate = date;
                updateSelectedDateDisplay();
                refreshCalendarSelection();
            }
        });

        recyclerViewCalendar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCalendar.setAdapter(calendarAdapter);
    }

    private void refreshCalendar() {
        calendarDays = generateCalendarDays();
        if (calendarAdapter != null) {
            // Buat adapter baru dengan data yang diperbarui
            calendarAdapter = new CalendarAdapter(calendarDays, selectedDate, new CalendarAdapter.OnDateClickListener() {
                @Override
                public void onDateClick(String date) {
                    selectedDate = date;
                    updateSelectedDateDisplay();
                    refreshCalendarSelection();
                }
            });
            recyclerViewCalendar.setAdapter(calendarAdapter);
        }
    }

    private void refreshCalendarSelection() {
        if (calendarAdapter != null) {
            // Update semua item untuk menandai yang terpilih
            for (MainActivity6.CalendarDay day : calendarDays) {
                day.setSelected(day.getFullDate().equals(selectedDate));
            }
            calendarAdapter.notifyDataSetChanged();
        }
    }

    private List<MainActivity6.CalendarDay> generateCalendarDays() {
        List<MainActivity6.CalendarDay> days = new ArrayList<>();

        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        int maxDaysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int day = 1; day <= maxDaysInMonth; day++) {
            Calendar dayCalendar = (Calendar) currentCalendar.clone();
            dayCalendar.set(Calendar.DAY_OF_MONTH, day);

            String date = getFormattedDate(dayCalendar);
            boolean isSelected = date.equals(selectedDate);

            // Format untuk display di horizontal calendar
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());

            String dayName = dayFormat.format(dayCalendar.getTime());
            String dateNumber = dateFormat.format(dayCalendar.getTime());

            days.add(new MainActivity6.CalendarDay(dayName, dateNumber, date, isSelected));
        }

        return days;
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvCurrentMonthYear.setText(monthFormat.format(currentCalendar.getTime()));
    }

    private void updateSelectedDateDisplay() {
        // Menggunakan resource string dengan placeholder untuk menghindari concatenation warning
        String selectedDateText = getString(R.string.selected_date_format, selectedDate);
        tvSelectedDate.setText(selectedDateText);
    }

    private void setupColorPicker() {
        colorContainer.removeAllViews();

        int size = dpToPx(40); // Sedikit lebih besar untuk accommodate border
        int margin = dpToPx(6);

        for (int i = 0; i < colorCodes.length; i++) {
            String colorCode = colorCodes[i];

            // Create outer container for border
            LinearLayout colorItemLayout = new LinearLayout(this);
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(size, size);
            containerParams.setMargins(margin, 0, margin, 0);
            colorItemLayout.setLayoutParams(containerParams);
            colorItemLayout.setOrientation(LinearLayout.VERTICAL);
            colorItemLayout.setPadding(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2)); // Padding untuk border

            // Apply border jika warna ini yang sedang dipilih
            if (Color.parseColor(colorCode) == selectedColor) {
                colorItemLayout.setBackgroundResource(R.drawable.color_circle_selected);
            }

            // Create inner color view
            View colorView = new View(this);
            LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            colorView.setLayoutParams(colorParams);

            // Set rounded corners untuk inner view
            colorView.setBackground(getResources().getDrawable(R.drawable.color_circle_background));
            colorView.getBackground().setTint(Color.parseColor(colorCode));

            // Add color view to container
            colorItemLayout.addView(colorView);

            // Add click listener
            final int position = i;
            final String finalColorCode = colorCode;
            colorItemLayout.setOnClickListener(v -> {
                selectedColor = Color.parseColor(finalColorCode);
                updateColorSelection(position);
            });

            colorContainer.addView(colorItemLayout);
        }
    }

    private void updateColorSelection(int selectedPosition) {
        // Reset semua border dan set border untuk yang dipilih
        for (int i = 0; i < colorContainer.getChildCount(); i++) {
            View colorItemLayout = colorContainer.getChildAt(i);

            if (i == selectedPosition) {
                // Set border hitam untuk warna yang dipilih
                colorItemLayout.setBackgroundResource(R.drawable.color_circle_selected);
            } else {
                // Hapus border untuk warna lain
                colorItemLayout.setBackgroundResource(0);
            }

            // Pastikan warna inner view tetap
            View colorView = ((LinearLayout) colorItemLayout).getChildAt(0);
            colorView.getBackground().setTint(Color.parseColor(colorCodes[i]));
        }
    }

    private void setupSaveButton() {
        btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void setupBackButton() {
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

    private void saveTask() {
        String taskName = etTaskName.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        // Validasi input menggunakan resource string
        if (taskName.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_task, Toast.LENGTH_SHORT).show();
            etTaskName.requestFocus();
            return;
        }

        if (startTime.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_start_time, Toast.LENGTH_SHORT).show();
            etStartTime.requestFocus();
            return;
        }

        if (endTime.isEmpty()) {
            Toast.makeText(this, R.string.please_enter_end_time, Toast.LENGTH_SHORT).show();
            etEndTime.requestFocus();
            return;
        }

        // Validasi time format
        if (!isValidTime(startTime) || !isValidTime(endTime)) {
            Toast.makeText(this, R.string.please_enter_valid_time, Toast.LENGTH_SHORT).show();
            return;
        }

        // Pastikan selectedColor sudah ter-set
        if (selectedColor == 0) {
            selectedColor = Color.parseColor(colorCodes[0]); // Fallback ke warna pertama
        }

        // Buat task baru dengan date yang dipilih
        Task newTask = new Task(taskName, selectedDate, startTime, endTime, selectedColor);

        // Simpan ke SharedPreferences
        List<Task> tasks = loadTasksFromStorage();
        tasks.add(newTask);

        String tasksJson = gson.toJson(tasks);
        sharedPreferences.edit().putString(TASKS_KEY, tasksJson).apply();

        // Menggunakan resource string untuk toast success
        Toast.makeText(this, R.string.task_saved_success, Toast.LENGTH_SHORT).show();

        // Kembali ke MainActivity3
        kembaliKeActivity3();
    }

    private boolean isValidTime(String time) {
        return time.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    private String getFormattedDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
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
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void kembaliKeActivity3() {
        Intent intent = new Intent(MainActivity6.this, MainActivity3.class);
        startActivity(intent);
        finish();
    }

    // Helper method untuk konversi dp ke px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // Method untuk reset color selection (jika diperlukan)
    private void resetColorSelection() {
        for (int i = 0; i < colorContainer.getChildCount(); i++) {
            View colorItemLayout = colorContainer.getChildAt(i);
            colorItemLayout.setBackgroundResource(0); // Hapus border
        }
    }

    // Model class untuk calendar day
    public static class CalendarDay {
        private String dayName;
        private String dateNumber;
        private String fullDate;
        private boolean isSelected;

        public CalendarDay(String dayName, String dateNumber, String fullDate, boolean isSelected) {
            this.dayName = dayName;
            this.dateNumber = dateNumber;
            this.fullDate = fullDate;
            this.isSelected = isSelected;
        }

        public String getDayName() {
            return dayName;
        }

        public String getDateNumber() {
            return dateNumber;
        }

        public String getFullDate() {
            return fullDate;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}