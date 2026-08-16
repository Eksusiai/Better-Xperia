package com.sonyericsson.calendar.util;
import com.sonymobile.calendar.SafeTime;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import com.sonymobile.calendar.Utils;
import com.sonymobile.lunar.lib.LunarContract;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ICalendarParser extends CalendarParser {
    private static String TAG = "ICalendarParser";
    private static ICalendarParser sInstance;

    private ICalendarParser() {
    }

    public static ICalendarParser getInstance() {
        if (sInstance == null) {
            sInstance = new ICalendarParser();
        }
        return sInstance;
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void handleTag(String str, String str2, boolean z) throws IOException {
        if (str == null) {
            if (this.currentItem != null) {
                this.currentItem.handleTag(null, str2, z);
                return;
            }
            return;
        }
        if (z) {
            this.currentItem.handleTag(null, str2, z);
            return;
        }
        if (str.equals(CalendarConstants.BEGIN)) {
            handleBegin(str2);
            return;
        }
        if (str.equals("END")) {
            handleEnd(str2);
            return;
        }
        if (str.equals(CalendarConstants.VERSION)) {
            handleVersion(str2);
            return;
        }
        if (!this.parsingOngoing || this.currentItem == null) {
            return;
        }
        if (((ICal) this.currentItem).isInsertingAlarm()) {
            ((ICal) this.currentItem).handleAlarmTag(str, str2);
        } else {
            this.currentItem.handleTag(str, str2, z);
        }
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void parseRow(String str, boolean z) throws Exception {
        String strSubstring;
        int iIndexOf = str.indexOf(CalendarConstants.COLON);
        int iIndexOf2 = str.indexOf(";");
        if (iIndexOf != -1 && iIndexOf2 != -1) {
            iIndexOf = Math.min(iIndexOf, iIndexOf2);
        } else if (iIndexOf == -1) {
            iIndexOf = iIndexOf2 != -1 ? iIndexOf2 : -1;
        }
        if (iIndexOf == -1 || z) {
            strSubstring = null;
        } else {
            strSubstring = str.substring(0, iIndexOf);
            str = str.substring(iIndexOf + 1);
            int iIndexOf3 = str.indexOf(CalendarConstants.COLON);
            if (iIndexOf == iIndexOf2 && strSubstring.equals(CalendarConstants.SUMMARY) && iIndexOf3 != -1) {
                str = str.substring(iIndexOf3 + 1);
            }
        }
        if (strSubstring != null && strSubstring.contains(";")) {
            strSubstring = strSubstring.substring(0, strSubstring.indexOf(";"));
        }
        handleTag(strSubstring, str, z);
    }

    private String formatLineBreaks(String str) {
        return str.replaceAll("(?<!\\\\)\\\\n", System.getProperty("line.separator")).replace("\\\\n", "\\n");
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void insertCalEvent(Context context, List<CalendarObject> list, long j) {
        for (CalendarObject calendarObject : list) {
            Time time = new SafeTime();
            time.set(calendarObject.start);
            if (!isEventDuplicate(calendarObject, EventLoaderService.getInstance().getEvents(Time.getJulianDay(calendarObject.start, time.gmtoff)))) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(LunarContract.EventsColumns.CALENDAR_ID, Long.valueOf(j));
                if (!TextUtils.isEmpty(calendarObject.summary)) {
                    contentValues.put(LunarContract.EventsColumns.TITLE, calendarObject.summary);
                }
                if (!TextUtils.isEmpty(calendarObject.description)) {
                    contentValues.put("description", formatLineBreaks(calendarObject.description));
                }
                if (!TextUtils.isEmpty(calendarObject.location)) {
                    contentValues.put(LunarContract.EventsColumns.EVENT_LOCATION, calendarObject.location);
                }
                if (!TextUtils.isEmpty(calendarObject.rrule)) {
                    contentValues.put("rrule", calendarObject.rrule);
                }
                contentValues.put(LunarContract.EventsColumns.HAS_ATTENDEE_DATA, (Integer) 1);
                contentValues.put(LunarContract.EventsColumns.EVENT_TIMEZONE, calendarObject.getTimeZone());
                handleEventTime(calendarObject, contentValues);
                ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
                arrayList.add(ContentProviderOperation.newInsert(CalendarContract.Events.CONTENT_URI).withValues(contentValues).build());
                ICal iCal = (ICal) calendarObject;
                addAttendees(iCal, arrayList);
                addAlarms(iCal, arrayList);
                try {
                    context.getContentResolver().applyBatch("com.android.calendar", arrayList);
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                } catch (IllegalArgumentException e3) {
                    Log.w(TAG, e3.getMessage());
                    this.handlers.poll().onReady(false);
                    return;
                }
            }
        }
        while (!this.handlers.isEmpty()) {
            this.handlers.remove().onReady((this.itemList == null || this.itemList.isEmpty()) ? false : true);
        }
    }

    private void handleEventTime(CalendarObject calendarObject, ContentValues contentValues) {
        if (String.valueOf(calendarObject.start).length() == 8) {
            String strValueOf = String.valueOf(calendarObject.start);
            int i = Integer.parseInt(strValueOf.substring(0, 4));
            calendarObject.start = Utils.getAllDayInMillis(Integer.parseInt(strValueOf.substring(6)), Integer.parseInt(strValueOf.substring(4, 6)), i);
            contentValues.put("allDay", (Integer) 1);
        }
        if (String.valueOf(calendarObject.end).length() == 8) {
            String strValueOf2 = String.valueOf(calendarObject.end);
            int i2 = Integer.parseInt(strValueOf2.substring(0, 4));
            calendarObject.end = Utils.getAllDayInMillis(Integer.parseInt(strValueOf2.substring(6)), Integer.parseInt(strValueOf2.substring(4, 6)), i2);
        }
        contentValues.put(LunarContract.EventsColumns.DTSTART, Long.valueOf(calendarObject.start));
        contentValues.put(LunarContract.EventsColumns.DTEND, Long.valueOf(calendarObject.end));
    }

    private void addAttendees(ICal iCal, ArrayList<ContentProviderOperation> arrayList) {
        ArrayList<ICal.Attendee> attendees = iCal.getAttendees();
        ContentValues contentValues = new ContentValues();
        for (ICal.Attendee attendee : attendees) {
            if (checkAttendee(attendee)) {
                contentValues.clear();
                contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_NAME, attendee.name);
                contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_EMAIL, attendee.mail);
                contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_RELATIONSHIP, Integer.valueOf(attendee.relationship));
                contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_TYPE, Integer.valueOf(attendee.type));
                contentValues.put(LunarContract.AttendeesColumns.ATTENDEE_STATUS, Integer.valueOf(attendee.status));
                arrayList.add(ContentProviderOperation.newInsert(CalendarContract.Attendees.CONTENT_URI).withValueBackReference("event_id", 0).withValues(contentValues).build());
            }
        }
    }

    private boolean checkAttendee(ICal.Attendee attendee) {
        return (attendee == null || attendee.name == null || attendee.mail == null) ? false : true;
    }

    private void addAlarms(ICal iCal, ArrayList<ContentProviderOperation> arrayList) {
        ArrayList<ICal.Alarm> alarms = iCal.getAlarms();
        ContentValues contentValues = new ContentValues();
        for (ICal.Alarm alarm : alarms) {
            contentValues.clear();
            if (alarm.reminderMinutes == -1) {
                return;
            }
            contentValues.put("minutes", Long.valueOf(alarm.reminderMinutes));
            contentValues.put("method", (Integer) 1);
            arrayList.add(ContentProviderOperation.newInsert(CalendarContract.Reminders.CONTENT_URI).withValueBackReference("event_id", 0).withValues(contentValues).build());
        }
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void handleEnd(String str) throws IOException {
        if (str.equals(CalendarConstants.VCALENDAR)) {
            if (this.parsingOngoing && this.currentItem == null) {
                this.parsingOngoing = false;
                return;
            }
            throw new IOException("Bad state of iCalendar object " + str);
        }
        if (str.equals(CalendarConstants.VEVENT)) {
            if (this.parsingOngoing && this.currentItem != null && this.currentItem.type == 2) {
                this.itemList.add(this.currentItem);
                this.currentItem = null;
                return;
            }
            throw new IOException("Bad state of iCalendar object " + str);
        }
        if (str.equals(CalendarConstants.VALARM)) {
            ((ICal) this.currentItem).closeAlarm();
            return;
        }
        if (str.equals(CalendarConstants.VTODO)) {
            if (this.parsingOngoing && this.currentItem != null && this.currentItem.type == 1) {
                this.itemList.add(this.currentItem);
                this.currentItem = null;
                return;
            }
            throw new IOException("Bad state of iCalendar object " + str);
        }
        throw new IOException("Unhandled object type " + str);
    }

    @Override // com.sonyericsson.calendar.util.CalendarParser
    protected void handleBegin(String str) throws IOException {
        if (str.equals(CalendarConstants.VCALENDAR)) {
            if (this.itemList == null && !this.parsingOngoing) {
                this.itemList = new LinkedList();
                this.parsingOngoing = true;
                return;
            }
            throw new IOException("Error parsing new object");
        }
        if (str.equals(CalendarConstants.VALARM)) {
            if (!this.parsingOngoing || this.itemList == null) {
                return;
            }
            ((ICal) this.currentItem).beginInsertAlarm();
            return;
        }
        if (this.currentItem != null) {
            throw new IOException("Error parsing new object");
        }
        if (str.equals(CalendarConstants.VTODO)) {
            this.currentItem = new ICal(1);
        } else if (str.equals(CalendarConstants.VEVENT)) {
            this.currentItem = new ICal(2);
        }
    }

    protected void handleVersion(String str) throws IOException {
        if (!str.equals(CalendarConstants.ICALENDAR_DEFAULT_VERSION)) {
            throw new IOException("Unsupported iCalendar version " + str);
        }
    }
}
