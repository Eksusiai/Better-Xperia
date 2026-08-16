package com.sonymobile.calendar.tasks.model;

/* JADX INFO: loaded from: classes2.dex */
public class TaskAccount {
    public int accountColor;
    public int id;
    public String name;
    public String type;
    public int visibility;

    public boolean isLocal() {
        return this.type.equalsIgnoreCase("LOCAL");
    }
}
