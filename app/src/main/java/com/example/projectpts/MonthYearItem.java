package com.example.projectpts;

public class MonthYearItem {
    private String label;
    private int value;
    private boolean selected;

    public MonthYearItem(String label, int value, boolean selected) {
        this.label = label;
        this.value = value;
        this.selected = selected;
    }

    // Getters and setters
    public String getLabel() { return label; }
    public int getValue() { return value; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}