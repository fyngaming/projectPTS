package com.example.projectpts;

import java.util.Date;

public class DateItem {
    private Date date;
    private int day;
    private boolean isCurrentMonth;
    private boolean isSelected;
    private boolean isToday;

    public DateItem(Date date, int day, boolean isCurrentMonth, boolean isSelected, boolean isToday) {
        this.date = date;
        this.day = day;
        this.isCurrentMonth = isCurrentMonth;
        this.isSelected = isSelected;
        this.isToday = isToday;
    }

    // Getters
    public Date getDate() { return date; }
    public int getDay() { return day; }
    public boolean isCurrentMonth() { return isCurrentMonth; }
    public boolean isSelected() { return isSelected; }
    public boolean isToday() { return isToday; }
}