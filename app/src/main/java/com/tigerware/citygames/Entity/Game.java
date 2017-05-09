package com.tigerware.citygames.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by User on 16.04.2017.
 */

public class Game implements Serializable {
    private int id;
    private Date startDate;
    private Date finishDate;

    private  int stageAmount;
    private ArrayList<Task> taskList;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

   public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStart_date() {
        return startDate;
    }

    public void setStart_date(Date start_date) {
        this.startDate = start_date;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public int getStageAmount() {
        return stageAmount;
    }

    public void setStageAmount(int stageAmount) {
        this.stageAmount = stageAmount;
    }





}
