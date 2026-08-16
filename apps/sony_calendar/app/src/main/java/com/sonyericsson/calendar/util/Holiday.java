package com.sonyericsson.calendar.util;

/* JADX INFO: loaded from: classes.dex */
public class Holiday {
    public String description;
    private int endDay;
    private int startDay;

    public Holiday(String str, int i, int i2) {
        this.description = str;
        this.startDay = i;
        this.endDay = i2;
    }

    public boolean contains(int i) {
        return this.startDay <= i && i <= this.endDay;
    }

    public int getStartDate() {
        return this.startDay;
    }

    public int getEndDate() {
        return this.endDay;
    }
}
