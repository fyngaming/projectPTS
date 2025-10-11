package com.example.projectpts;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity6 extends AppCompatActivity {

    private EditText etTaskName, etStartTime, etEndTime;
    private LinearLayout colorContainer;
    private TextView tvCurrentMonthYear, tvSelectedDate;
    private Button btnSaveTask;
    private ImageButton btnPrevMonth, btnNextMonth;
    private RecyclerView recyclerViewCalendar;

    private Calendar selectedCalendar;
    private Calendar currentCalendar;
    private int selectedColor = Color.parseColor("#4CAF50");
    private DateAdapter dateAdapter;

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final String PREFS_NAME = "TaskPrefs";
    private static final String TASKS_KEY = "tasks";

    private final int[] colors = {
            Color.parseColor("#FF5252"), // Red
            Color.parseColor("#FF9800"), // Orange
            Color.parseColor("#FFEB3B"), // Yellow
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#9C27B0"), // Purple
    };

    // Date format
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        // Initialize SharedPreferences and Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        setupBackButton();
        setupCalendar();
        setupColorPicker();
        setupTimePickers();
        setupSaveButton();
        setupMonthNavigation();
        setupSwipeGestures();
    }

    private void initViews() {
        etTaskName = findViewById(R.id.etTaskName);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        colorContainer = findViewById(R.id.colorContainer);
        tvCurrentMonthYear = findViewById(R.id.tvCurrentMonthYear);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
        recyclerViewCalendar = findViewById(R.id.recyclerViewCalendar);

        // Setup RecyclerView
        recyclerViewCalendar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCalendar.setHasFixedSize(true);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> kembaliKeActivity3());

        // Initialize calendars
        selectedCalendar = Calendar.getInstance();
        currentCalendar = Calendar.getInstance();

        // Set default selection to today
        selectedCalendar.setTime(new Date());
        currentCalendar.setTime(new Date());
    }

    private void setupBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                kembaliKeActivity3();
            }
        });
    }

    private void setupCalendar() {
        updateCalendarDisplay();
    }

    private void setupMonthNavigation() {
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarDisplay();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarDisplay();
        });

        // Month/Year picker
        tvCurrentMonthYear.setOnClickListener(v -> showMonthYearPicker());
    }

    private void setupSwipeGestures() {
        final float[] startX = new float[1];

        recyclerViewCalendar.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    startX[0] = event.getX();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    float deltaX = endX - startX[0];

                    // Minimum swipe distance
                    if (Math.abs(deltaX) > 100) {
                        if (deltaX > 0) {
                            // Swipe right - previous month
                            currentCalendar.add(Calendar.MONTH, -1);
                        } else {
                            // Swipe left - next month
                            currentCalendar.add(Calendar.MONTH, 1);
                        }
                        updateCalendarDisplay();
                    }
                    break;
            }
            return false;
        });
    }

    private void showMonthYearPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Month and Year");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_month_year_picker, null);
        builder.setView(dialogView);

        RecyclerView rvMonths = dialogView.findViewById(R.id.rvMonths);
        RecyclerView rvYears = dialogView.findViewById(R.id.rvYears);

        setupMonthPicker(rvMonths);
        setupYearPicker(rvYears);

        builder.setPositiveButton("OK", (dialog, which) -> {
            updateCalendarDisplay();
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void setupMonthPicker(RecyclerView recyclerView) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        List<MonthYearItem> monthList = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            monthList.add(new MonthYearItem(months[i], i, false));
        }

        // Select current month
        monthList.get(currentCalendar.get(Calendar.MONTH)).setSelected(true);

        MonthYearAdapter adapter = new MonthYearAdapter(monthList, (position, item) -> {
            currentCalendar.set(Calendar.MONTH, position);
            updateMonthYearText();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupYearPicker(RecyclerView recyclerView) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<MonthYearItem> yearList = new ArrayList<>();

        // Show 10 years before and after current year
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearList.add(new MonthYearItem(String.valueOf(i), i, false));
        }

        // Select current year
        for (MonthYearItem item : yearList) {
            if (item.getValue() == currentCalendar.get(Calendar.YEAR)) {
                item.setSelected(true);
                break;
            }
        }

        MonthYearAdapter adapter = new MonthYearAdapter(yearList, (position, item) -> {
            currentCalendar.set(Calendar.YEAR, item.getValue());
            updateMonthYearText();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void updateCalendarDisplay() {
        updateMonthYearText();
        setupCalendarDates();
    }

    private void updateMonthYearText() {
        tvCurrentMonthYear.setText(monthYearFormat.format(currentCalendar.getTime()));
    }

    private void setupCalendarDates() {
        List<DateItem> dateList = generateDateList();
        dateAdapter = new DateAdapter(dateList, this::onDateSelected);
        recyclerViewCalendar.setAdapter(dateAdapter);

        // Update selected date display
        updateSelectedDateDisplay();
    }

    private List<DateItem> generateDateList() {
        List<DateItem> dateList = new ArrayList<>();
        Calendar tempCalendar = (Calendar) currentCalendar.clone();

        // Set to first day of month
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // Add some days from previous month
        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
        tempCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek + 1);

        // Generate 42 days (6 weeks)
        for (int i = 0; i < 42; i++) {
            boolean isCurrentMonth = tempCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH);
            boolean isSelected = isSameDay(tempCalendar, selectedCalendar);
            boolean isToday = isSameDay(tempCalendar, Calendar.getInstance());

            dateList.add(new DateItem(
                    tempCalendar.getTime(),
                    tempCalendar.get(Calendar.DAY_OF_MONTH),
                    isCurrentMonth,
                    isSelected,
                    isToday
            ));

            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dateList;
    }

    private void onDateSelected(DateItem dateItem) {
        selectedCalendar.setTime(dateItem.getDate());
        updateSelectedDateDisplay();
        setupCalendarDates(); // Refresh to update selection

        Toast.makeText(this, "Selected: " + dateFormat.format(dateItem.getDate()), Toast.LENGTH_SHORT).show();
    }

    private void updateSelectedDateDisplay() {
        tvSelectedDate.setText("Selected: " + dateFormat.format(selectedCalendar.getTime()));
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void setupColorPicker() {
        colorContainer.removeAllViews();

        for (int i = 0; i < colors.length; i++) {
            final int color = colors[i];

            // Create container for color circle
            LinearLayout container = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(48), dpToPx(48));
            params.setMargins(dpToPx(8), 0, dpToPx(8), 0);
            container.setLayoutParams(params);
            container.setGravity(Gravity.CENTER);
            container.setBackgroundResource(android.R.drawable.btn_default_small);

            // Create color circle
            View colorCircle = new View(this);
            LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
            colorCircle.setLayoutParams(circleParams);
            colorCircle.setBackgroundColor(color);

            // Set initial selection (Green selected by default)
            if (i == 3) {
                container.setBackgroundResource(android.R.drawable.alert_dark_frame);
                selectedColor = color;
            }

            container.setOnClickListener(v -> {
                // Reset all color borders
                for (int j = 0; j < colorContainer.getChildCount(); j++) {
                    View child = colorContainer.getChildAt(j);
                    child.setBackgroundResource(android.R.drawable.btn_default_small);
                }

                // Set selected color border
                container.setBackgroundResource(android.R.drawable.alert_dark_frame);
                selectedColor = color;

                Toast.makeText(MainActivity6.this, "Color selected", Toast.LENGTH_SHORT).show();
            });

            container.addView(colorCircle);
            colorContainer.addView(container);
        }
    }

    private void setupTimePickers() {
        // Set default times
        etStartTime.setText("15:00");
        etEndTime.setText("22:00");

        etStartTime.setFocusable(false);
        etEndTime.setFocusable(false);
        etStartTime.setClickable(true);
        etEndTime.setClickable(true);

        etStartTime.setOnClickListener(v -> showTimePicker(etStartTime, "15:00"));
        etEndTime.setOnClickListener(v -> showTimePicker(etEndTime, "22:00"));
    }

    private void showTimePicker(final EditText editText, String defaultTime) {
        String currentTime = editText.getText().toString();
        int hour, minute;

        if (!currentTime.isEmpty()) {
            String[] timeParts = currentTime.split(":");
            hour = Integer.parseInt(timeParts[0]);
            minute = Integer.parseInt(timeParts[1]);
        } else {
            String[] defaultParts = defaultTime.split(":");
            hour = Integer.parseInt(defaultParts[0]);
            minute = Integer.parseInt(defaultParts[1]);
        }

        TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            editText.setText(time);
        }, hour, minute, true);

        timePicker.setTitle("Select Time");
        timePicker.show();
    }

    private void setupSaveButton() {
        btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String taskName = etTaskName.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();

        // Validation
        if (taskName.isEmpty()) {
            showError(etTaskName, "Please enter task name");
            return;
        }

        if (startTime.isEmpty()) {
            showError(etStartTime, "Please select start time");
            return;
        }

        if (endTime.isEmpty()) {
            showError(etEndTime, "Please select end time");
            return;
        }

        // Save task logic
        String selectedDateStr = dateFormat.format(selectedCalendar.getTime());

        // Create task object
        Task newTask = new Task(taskName, selectedDateStr, startTime, endTime, selectedColor);

        // Save to SharedPreferences
        saveTaskToStorage(newTask);

        String message = String.format("Task '%s' saved successfully!\nDate: %s\nTime: %s - %s",
                taskName, selectedDateStr, startTime, endTime);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Navigate back to MainActivity3 after save
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity6.this, MainActivity3.class);
            intent.putExtra("newTaskAdded", true);
            startActivity(intent);
            finish();
        }, 1500);
    }

    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
    }

    private void saveTaskToStorage(Task newTask) {
        // Load existing tasks
        List<Task> taskList = loadTasksFromStorage();

        // Add new task
        taskList.add(newTask);

        // Save back to SharedPreferences
        String tasksJson = gson.toJson(taskList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TASKS_KEY, tasksJson);
        editor.apply();

        Log.d("TASK_SAVE", "Task saved: " + newTask.getTaskName() +
                " | Date: " + newTask.getDate() +
                " | Time: " + newTask.getStartTime() + " - " + newTask.getEndTime() +
                " | Total tasks: " + taskList.size());
    }

    private List<Task> loadTasksFromStorage() {
        String tasksJson = sharedPreferences.getString(TASKS_KEY, "");
        if (tasksJson.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> taskList = gson.fromJson(tasksJson, type);
        return taskList != null ? taskList : new ArrayList<>();
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
}