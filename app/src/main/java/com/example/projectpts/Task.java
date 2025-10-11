package com.example.projectpts;

import java.io.Serializable;

public class Task implements Serializable {
    private String id;
    private String taskName;
    private String date;
    private String startTime;
    private String endTime;
    private int color;
    private long timestamp;
    private boolean completed;

    public Task() {
        // Default constructor
    }

    public Task(String taskName, String date, String startTime, String endTime, int color) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.taskName = taskName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.color = color;
        this.timestamp = System.currentTimeMillis();
        this.completed = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}