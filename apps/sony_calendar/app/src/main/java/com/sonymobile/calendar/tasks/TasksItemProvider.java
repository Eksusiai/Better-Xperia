package com.sonymobile.calendar.tasks;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class TasksItemProvider {
    private static TasksItemProvider instance;
    private ArrayList<Long> mTaskIds = new ArrayList<>();

    public static TasksItemProvider getInstance() {
        if (instance == null) {
            instance = new TasksItemProvider();
        }
        return instance;
    }

    public void setTaskListIds(ArrayList<Long> arrayList) {
        this.mTaskIds = arrayList;
    }

    public ArrayList<Long> getTaskIds() {
        return this.mTaskIds;
    }
}
