package com.sonyericsson.calendar.util;

/* JADX INFO: loaded from: classes.dex */
public abstract class CalendarObject {
    public String categories;
    public String description;
    public String location;
    protected String previousTag;
    public String priority;
    public String rrule;
    public String summary;
    public int type;
    public String uid;
    public long start = -1;
    public long end = -1;

    protected abstract String getTimeZone();

    protected abstract void handleDescription(String str, boolean z);

    protected abstract void handleRRule(String str);

    protected abstract void handleSummary(String str, boolean z);

    protected abstract void handleVEventTag(String str, String str2, boolean z);

    protected abstract long parseTime(String str, boolean z);

    public CalendarObject(int i) {
        this.type = i;
    }

    protected void handleTag(String str, String str2, boolean z) {
        if (this.type == 2) {
            handleVEventTag(str, str2, z);
        } else {
            handleVTodoTag(str, str2, z);
        }
    }

    protected void handleVTodoTag(String str, String str2, boolean z) {
        if (str == null) {
            str = this.previousTag;
        }
        str.hashCode();
        switch (str) {
            case "LOCATION":
                handleLocation(str2, z);
                break;
            case "DTSTART":
                handleDTStart(str2, z);
                break;
            case "SUMMARY":
                handleSummary(str2, z);
                break;
            case "PRIORITY":
                handlePriority(str2, z);
                break;
            case "UID":
                handleUID(str2, z);
                break;
            case "DTEND":
                handleDTEnd(str2, z);
                break;
            case "RRULE":
                handleRRule(str2);
                break;
            case "DESCRIPTION":
                handleDescription(str2, z);
                break;
            case "CATEGORIES":
                handleCategories(str2, z);
                break;
        }
        this.previousTag = str;
    }

    protected void handlePriority(String str, boolean z) {
        if (z) {
            str = this.priority + str;
        }
        this.priority = str;
    }

    protected void handleLocation(String str, boolean z) {
        if (z) {
            str = this.location + str;
        }
        this.location = str;
    }

    protected void handleUID(String str, boolean z) {
        if (z) {
            str = this.uid + str;
        }
        this.uid = str;
    }

    protected void handleCategories(String str, boolean z) {
        if (z) {
            str = this.categories + str;
        }
        this.categories = str;
    }

    protected void handleDTStart(String str, boolean z) {
        this.start = parseTime(str, z);
    }

    protected void handleDTEnd(String str, boolean z) {
        this.end = parseTime(str, z);
    }
}
