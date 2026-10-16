package com.example.monstergym.model;

public class CalendarDay {
    private final int year;
    private final int month; // 0-indexed
    private final int day;
    private final boolean isCurrentMonth;
    private final String dateStr; // YYYY-MM-DD
    private final boolean isTrained;
    private final boolean isSelected;
    private final String abbreviation;

    public CalendarDay(int year, int month, int day, boolean isCurrentMonth, String dateStr, boolean isTrained, boolean isSelected, String abbreviation) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.isCurrentMonth = isCurrentMonth;
        this.dateStr = dateStr;
        this.isTrained = isTrained;
        this.isSelected = isSelected;
        this.abbreviation = abbreviation;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public String getDateStr() {
        return dateStr;
    }

    public boolean isTrained() {
        return isTrained;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public boolean isSelected() {
        return isSelected;
    }
}