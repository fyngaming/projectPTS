package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity5 extends AppCompatActivity {

    private LinearLayout tasksContainer;
    private LinearLayout calendarGrid;
    private TextView tvMonthYear, tvSelectedDate, tvSelectedDateHeader;
    private ImageButton btnPrevMonth, btnNextMonth;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final String TAG = "MainActivity5";

    private static final String PREFS_NAME = "TaskPrefs";
    private static final String TASKS_KEY = "tasks";
    private static final String COMPLETED_TASKS_KEY = "completed_tasks";

    private Set<String> completedTasks;
    private Calendar currentCalendar;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Initialize SharedPreferences dan Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        // Load completed tasks
        completedTasks = sharedPreferences.getStringSet(COMPLETED_TASKS_KEY, new HashSet<>());

        // Setup calendar dengan tanggal saat ini
        currentCalendar = Calendar.getInstance();
        selectedDate = getFormattedDate(currentCalendar);

        initViews();
        setupBackButton();
        setupFooterNavigation();
        setupCalendar();
        loadAndDisplayTasksForSelectedDate();
    }

    private void initViews() {
        tasksContainer = findViewById(R.id.tasksContainer);
        calendarGrid = findViewById(R.id.calendarGrid);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedDateHeader = findViewById(R.id.tvSelectedDateHeader);
        btnPrevMonth = findViewById(R.id.btnPrevMonth);
        btnNextMonth = findViewById(R.id.btnNextMonth);
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

        // Logo Notes di footer - pergi ke MainActivity7 (INBOX/REMINDER)
        findViewById(R.id.footer_notes).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity5.this, MainActivity7.class);
            startActivity(intent);
        });
    }

    private void setupCalendar() {
        updateMonthYearDisplay();
        generateCalendar();
        updateSelectedDateDisplay();

        // Setup month navigation
        btnPrevMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNextMonth.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    private void updateCalendar() {
        updateMonthYearDisplay();
        generateCalendar();
        loadAndDisplayTasksForSelectedDate();
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(monthFormat.format(currentCalendar.getTime()).toUpperCase());
    }

    private void generateCalendar() {
        calendarGrid.removeAllViews();

        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Create weeks
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setStretchAllColumns(true);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));

        TableRow currentRow = new TableRow(this);
        currentRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        // Add empty cells for days before the first day of month
        for (int i = 1; i < firstDayOfWeek; i++) {
            TextView emptyView = createEmptyDateView();
            currentRow.addView(emptyView);
        }

        // Add days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            Calendar dayCalendar = (Calendar) currentCalendar.clone();
            dayCalendar.set(Calendar.DAY_OF_MONTH, day);

            String date = getFormattedDate(dayCalendar);
            boolean isSelected = date.equals(selectedDate);
            boolean hasTasks = hasTasksForDate(date);

            TextView dateView = createDateView(day, date, isSelected, hasTasks);
            currentRow.addView(dateView);

            // Start new row after 7 days
            if ((firstDayOfWeek - 1 + day) % 7 == 0) {
                tableLayout.addView(currentRow);
                currentRow = new TableRow(this);
                currentRow.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                ));
            }
        }

        // Add remaining empty cells
        int remainingCells = 7 - currentRow.getChildCount();
        for (int i = 0; i < remainingCells; i++) {
            TextView emptyView = createEmptyDateView();
            currentRow.addView(emptyView);
        }

        tableLayout.addView(currentRow);
        calendarGrid.addView(tableLayout);
    }

    private TextView createDateView(int day, final String date, boolean isSelected, boolean hasTasks) {
        TextView dateView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f
        );
        params.setMargins(2, 2, 2, 2);
        dateView.setLayoutParams(params);

        dateView.setText(String.valueOf(day));
        dateView.setGravity(Gravity.CENTER);
        dateView.setPadding(8, 12, 8, 12);
        dateView.setTextSize(12);

        // Styling berdasarkan selection dan tasks
        if (isSelected) {
            dateView.setBackgroundResource(R.drawable.selected_date_background);
            dateView.setTextColor(Color.WHITE);
        } else if (hasTasks) {
            dateView.setBackgroundResource(R.drawable.has_tasks_date_background);
            dateView.setTextColor(Color.WHITE);
        } else {
            dateView.setBackgroundResource(R.drawable.normal_date_background);
            dateView.setTextColor(Color.parseColor("#2C3E50"));
        }

        // Click listener
        dateView.setOnClickListener(v -> {
            selectedDate = date;
            updateCalendar();
            loadAndDisplayTasksForSelectedDate();
        });

        return dateView;
    }

    private TextView createEmptyDateView() {
        TextView emptyView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f
        );
        params.setMargins(2, 2, 2, 2);
        emptyView.setLayoutParams(params);
        emptyView.setBackgroundColor(Color.TRANSPARENT);
        return emptyView;
    }

    private boolean hasTasksForDate(String date) {
        List<Task> allTasks = loadTasksFromStorage();
        for (Task task : allTasks) {
            if (task.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    private void loadAndDisplayTasksForSelectedDate() {
        List<Task> allTasks = loadTasksFromStorage();
        List<Task> filteredTasks = filterTasksByDate(allTasks, selectedDate);

        Log.d(TAG, "Loading tasks for date: " + selectedDate + ", Found: " + filteredTasks.size());

        if (filteredTasks.isEmpty()) {
            showEmptyState();
        } else {
            displayTasks(filteredTasks);
        }
    }

    private List<Task> filterTasksByDate(List<Task> tasks, String date) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDate().equals(date)) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    private void updateSelectedDateDisplay() {
        tvSelectedDate.setText(selectedDate);

        // Update header berdasarkan apakah hari ini atau bukan
        Calendar today = Calendar.getInstance();
        String todayFormatted = getFormattedDate(today);

        if (selectedDate.equals(todayFormatted)) {
            tvSelectedDateHeader.setText("Today's Tasks: ");
        } else {
            tvSelectedDateHeader.setText("Tasks for: ");
        }
    }

    private String getFormattedDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
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
        tvEmpty.setText("No tasks for " + selectedDate);
        tvEmpty.setTextColor(Color.GRAY);
        tvEmpty.setTextSize(16);
        tvEmpty.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        tvEmpty.setPadding(0, dpToPx(25), 0, dpToPx(25));

        tasksContainer.addView(tvEmpty);
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

    private void kembaliKeMainActivity3() {
        Intent intent = new Intent(MainActivity5.this, MainActivity3.class);
        startActivity(intent);
        finish(); // Tutup activity ini agar tidak menumpuk di back stack
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data ketika kembali ke activity ini
        updateCalendar();
        loadAndDisplayTasksForSelectedDate();
    }

    // Helper method untuk konversi dp ke px
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}