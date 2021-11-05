package com.example.all_habits;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Date;

/**
 * Creates a Habit
 * @author Elena
 * @Version 1.0
 */
public class Habit {

    private String habitName;
    private String reason;
    private ArrayList<String> habitDays;
    private String startDate;
    private int habitNum;
    private Boolean Private;

    /**
     * Constructor for creating the habit
     *
     * @param habitName
     * @param reason
     * @param habitDays
     * @param startDate
     * @param habitNum
     * @param Private
     */
    Habit(String habitName, String reason, ArrayList<String> habitDays, String startDate,
          int habitNum, Boolean Private){

        this.habitName= habitName;
        this.reason = reason;
        this.habitDays = habitDays;
        this.startDate = startDate;
        this.habitNum = habitNum;
        this.Private = Private;

    }

    public Habit() {

    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ArrayList<String> getHabitDays() {
        return habitDays;
    }

    public void setHabitDays(ArrayList<String> habitDays) {
        this.habitDays = habitDays;

    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getHabitNum() {
        return habitNum;
    }

    public void setHabitNum(int habitNum) {
        this.habitNum = habitNum;
    }

    public Boolean getPrivate() {
        return Private;
    }

    public void setPrivate(Boolean aPrivate) {
        Private = aPrivate;
    }
}
