package com.sonyericsson.calendar.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class ICal extends CalendarObject {
    private ArrayList<Alarm> alarmList;
    private String attendee;
    private ArrayList<Attendee> attendeeList;
    private Alarm currentAlarm;
    private boolean insertingAlarm;
    private String standardDateFormat;
    private String timezoneId;

    public static class Alarm {
        public long reminderMinutes;
    }

    public static class Attendee {
        public String mail;
        public String name;
        public int relationship;
        public int status;
        public int type;
    }

    public ICal(int i) {
        super(i);
        this.alarmList = new ArrayList<>();
        this.attendeeList = new ArrayList<>();
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleRRule(String str) {
        if (str != null) {
            this.rrule = str;
        }
    }

    public ArrayList<Attendee> getAttendees() {
        return this.attendeeList;
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleVEventTag(String str, String str2, boolean z) {
        if (str == null) {
            str = this.previousTag;
        }
        if (!z && this.previousTag != null && (this.previousTag.equals(CalendarConstants.ATTENDEE) || this.previousTag.equals(CalendarConstants.ORGANIZER))) {
            addAttendee(this.previousTag);
        }
        if (str.equals(CalendarConstants.CATEGORIES)) {
            handleCategories(str2, z);
        } else if (str.equals(CalendarConstants.DTSTART)) {
            handleDTStart(str2, z);
        } else if (str.equals(CalendarConstants.DTEND)) {
            handleDTEnd(str2, z);
        } else if (str.equals(CalendarConstants.SUMMARY)) {
            handleSummary(str2, z);
        } else if (str.equals(CalendarConstants.DESCRIPTION)) {
            handleDescription(str2, z);
        } else if (str.equals(CalendarConstants.LOCATION)) {
            handleLocation(str2, z);
        } else if (str.equals(CalendarConstants.PRIORITY)) {
            handlePriority(str2, z);
        } else if (str.equals(CalendarConstants.RRULE)) {
            handleRRule(str2);
        } else if (str.equals(CalendarConstants.UID)) {
            handleUID(str2, z);
        } else if (str.equals(CalendarConstants.ATTENDEE) || str.equals(CalendarConstants.ORGANIZER)) {
            handleAttendee(str2, z);
        }
        this.previousTag = str;
    }

    private void addAttendee(String str) {
        Attendee attendee = new Attendee();
        if (str.equals(CalendarConstants.ORGANIZER)) {
            attendee.relationship = 2;
        } else if (str.equals(CalendarConstants.ATTENDEE)) {
            attendee.relationship = 1;
        } else {
            attendee.relationship = 0;
        }
        for (String str2 : this.attendee.split(";")) {
            handleAttendeeParameter(attendee, str2);
        }
        this.attendeeList.add(attendee);
    }

    private void handleAttendeeParameter(Attendee attendee, String str) {
        int iIndexOf = str.indexOf(CalendarConstants.COLON);
        if (!str.startsWith("mailto:") && iIndexOf != -1) {
            handleAttendeeParameter(attendee, str.substring(iIndexOf + 1));
            handleAttendeeParameter(attendee, str.substring(0, iIndexOf));
            return;
        }
        if (str.startsWith(CalendarConstants.COMMON_NAME)) {
            attendee.name = removeQuotes(str.substring(3));
            return;
        }
        if (str.toLowerCase(Locale.US).startsWith("mailto:")) {
            attendee.mail = removeQuotes(str.toLowerCase(Locale.US).substring(7));
            return;
        }
        if (str.startsWith(CalendarConstants.ROLE)) {
            if (str.substring(5).equals(CalendarConstants.REQ_PARTICIPANT)) {
                attendee.type = 1;
                return;
            } else {
                attendee.type = 2;
                return;
            }
        }
        if (str.startsWith(CalendarConstants.STATUS)) {
            String strSubstring = str.substring(6);
            if (strSubstring.equals(CalendarConstants.ACCEPTED)) {
                attendee.status = 1;
                return;
            }
            if (strSubstring.equals(CalendarConstants.DECLINED)) {
                attendee.status = 2;
            } else if (strSubstring.equals(CalendarConstants.TENTATIVE)) {
                attendee.status = 4;
            } else {
                attendee.status = 0;
            }
        }
    }

    private String removeQuotes(String str) {
        if (str.startsWith("\"") || str.startsWith("'")) {
            str = str.substring(1);
        }
        return (str.endsWith("\"") || str.endsWith("'")) ? str.substring(0, str.length() - 1) : str;
    }

    private void handleAttendee(String str, boolean z) {
        if (z) {
            str = this.attendee + str;
        }
        this.attendee = str;
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleDescription(String str, boolean z) {
        if (z) {
            str = this.description + str;
        }
        this.description = str;
    }

    public void beginInsertAlarm() {
        if (this.currentAlarm == null) {
            this.insertingAlarm = true;
            this.currentAlarm = new Alarm();
        }
    }

    public void closeAlarm() {
        this.insertingAlarm = false;
        Alarm alarm = this.currentAlarm;
        if (alarm != null) {
            this.alarmList.add(alarm);
            this.currentAlarm = null;
        }
    }

    public ArrayList<Alarm> getAlarms() {
        return this.alarmList;
    }

    public boolean isInsertingAlarm() {
        return this.insertingAlarm;
    }

    public void handleAlarmTag(String str, String str2) {
        if (str.equals(CalendarConstants.ALARM_TRIGGER)) {
            if (str2.startsWith(CalendarConstants.ALARM_RELATED)) {
                int iIndexOf = str2.indexOf("=");
                int iIndexOf2 = str2.indexOf(CalendarConstants.COLON);
                if (iIndexOf != -1 && iIndexOf2 != -1) {
                    String strSubstring = str2.substring(iIndexOf + 1, iIndexOf2);
                    str2 = str2.substring(iIndexOf2 + 1);
                    if (strSubstring.equals("END")) {
                        this.currentAlarm.reminderMinutes = -1L;
                        return;
                    }
                }
            }
            this.currentAlarm.reminderMinutes = getRelativeReminderTime(str2);
        }
    }

    private long getRelativeReminderTime(String str) {
        int i;
        int i2;
        if (str.startsWith(CalendarConstants.HYPHEN)) {
            String strSubstring = str.substring(1);
            if (strSubstring.startsWith(CalendarConstants.RELATIVE_TIME)) {
                String strSubstring2 = strSubstring.substring(2);
                int iIndexOf = strSubstring2.indexOf(CalendarConstants.HOURS);
                if (iIndexOf != -1) {
                    i = Integer.parseInt(strSubstring2.substring(0, iIndexOf));
                    strSubstring2 = strSubstring2.substring(iIndexOf + 1);
                } else {
                    i = 0;
                }
                int iIndexOf2 = strSubstring2.indexOf("M");
                if (iIndexOf2 != -1) {
                    i2 = Integer.parseInt(strSubstring2.substring(0, iIndexOf2));
                    strSubstring2 = strSubstring2.substring(iIndexOf2 + 1);
                } else {
                    i2 = 0;
                }
                int iIndexOf3 = strSubstring2.indexOf(CalendarConstants.SECONDS);
                return (((((long) i) * 3600000) + (((long) i2) * 60000)) + (((long) (iIndexOf3 != -1 ? Integer.parseInt(strSubstring2.substring(0, iIndexOf3)) : 0)) * 1000)) / 60000;
            }
        }
        return -1L;
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected long parseTime(String str, boolean z) {
        Date date;
        if (str.length() == 8) {
            return Long.parseLong(str);
        }
        int iIndexOf = str.indexOf(CalendarConstants.COLON);
        this.standardDateFormat = z ? this.standardDateFormat + str : str;
        if (iIndexOf != -1 && !z) {
            if (str.startsWith(CalendarConstants.TZID)) {
                this.timezoneId = str.substring(5, iIndexOf).replace("\"", " ");
                this.standardDateFormat = str.substring(iIndexOf + 1);
            } else if (str.startsWith(CalendarConstants.VALUE_DATE)) {
                return Long.parseLong(str.substring(iIndexOf + 1));
            }
        }
        try {
            String str2 = this.standardDateFormat;
            if (str2.charAt(str2.length() - 1) == 'Z') {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CalendarConstants.CALENDAR_DATE_FORMAT, Locale.US);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = simpleDateFormat.parse(this.standardDateFormat);
            } else {
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(CalendarConstants.CALENDAR_DATE_FORMAT_WITHOUT_TIMEZONE, Locale.US);
                String str3 = this.timezoneId;
                if (str3 != null) {
                    simpleDateFormat2.setTimeZone(TimeZone.getTimeZone(str3));
                } else {
                    simpleDateFormat2.setTimeZone(TimeZone.getDefault());
                }
                date = simpleDateFormat2.parse(this.standardDateFormat);
            }
            return date.getTime();
        } catch (ParseException unused) {
            return -1L;
        }
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected void handleSummary(String str, boolean z) {
        if (z) {
            str = this.summary + str;
        }
        this.summary = str;
    }

    @Override // com.sonyericsson.calendar.util.CalendarObject
    protected String getTimeZone() {
        String str = this.timezoneId;
        return str == null ? TimeZone.getDefault().getID() : str;
    }
}
