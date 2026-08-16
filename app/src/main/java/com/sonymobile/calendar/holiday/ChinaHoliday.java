package com.sonymobile.calendar.holiday;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class ChinaHoliday {
    private String mName = "";
    private String mDescription = "";
    private String mType = "";
    private ArrayList<String> mHolidayList = new ArrayList<>();
    private ArrayList<String> mWorkdayList = new ArrayList<>();

    public void setName(String str) {
        this.mName = str;
    }

    public String getName() {
        return this.mName;
    }

    public void setDescription(String str) {
        this.mDescription = str;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public void setType(String str) {
        this.mType = str;
    }

    public String getType() {
        return this.mType;
    }

    public void addHoliday(String str) {
        this.mHolidayList.add(str);
    }

    public void addWorkday(String str) {
        this.mWorkdayList.add(str);
    }

    public int checkHolidayOrWorkday(String str) {
        if (this.mHolidayList.contains(str)) {
            return "nation".equals(this.mType) ? 0 : -1;
        }
        return this.mWorkdayList.contains(str) ? 1 : -1;
    }

    public String getHolidaySpecial(String str) {
        return this.mHolidayList.contains(str) ? this.mDescription : "";
    }
}
