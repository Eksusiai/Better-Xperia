package com.sonymobile.calendar;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarItem {
    public String accountName;
    public String accountType;
    public long calendarId;
    public int color;
    public String displayAccountType;
    public String displayName;
    public boolean isVisible;
    public String ownerAccount;

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CalendarItem calendarItem = (CalendarItem) obj;
        return this.calendarId == calendarItem.calendarId && this.isVisible == calendarItem.isVisible;
    }

    public int hashCode() {
        long j = this.calendarId;
        return (((int) (j ^ (j >>> 32))) * 31) + (this.isVisible ? 1 : 0);
    }
}
