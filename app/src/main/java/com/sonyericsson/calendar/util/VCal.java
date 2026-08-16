package com.sonyericsson.calendar.util;

import android.text.TextUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class VCal extends CalendarObject {
    public VCal(int i) {
        super(i);
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleRRule(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            this.rrule = RecurrenceRuleParser.parseRruleToRFC2445(str);
        } catch (IllegalArgumentException unused) {
            this.rrule = null;
        }
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleVEventTag(String str, String str2, boolean z) {
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

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleSummary(String str, boolean z) {
        if (z) {
            str = this.summary + str;
        }
        this.summary = str;
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleDescription(String str, boolean z) {
        if (z) {
            str = this.description + str;
        }
        this.description = str;
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected long parseTime(String str, boolean z) {
        try {
            try {
                Date date = new SimpleDateFormat(CalendarConstants.CALENDAR_DATE_FORMAT).parse(str);
                return date.getTime() + getTimeZoneOffset(date);
            } catch (Exception unused) {
                return new SimpleDateFormat(CalendarConstants.CALENDAR_DATE_FORMAT_WITHOUT_TIMEZONE).parse(str).getTime();
            }
        } catch (Exception unused2) {
            return -1L;
        }
    }

    public static long getTimeZoneOffset(Date date) {
        int rawOffset;
        if (TimeZone.getDefault().inDaylightTime(date)) {
            rawOffset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        } else {
            rawOffset = TimeZone.getDefault().getRawOffset();
        }
        return rawOffset;
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected String getTimeZone() {
        return TimeZone.getDefault().getID();
    }
}
