package com.sonymobile.calendar.widget;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.Time;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.birthday.ContactBirthday;
import com.sonymobile.calendar.birthday.ContactBirthdayLoaderBinder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarAppWidgetModel {
    private static final boolean LOGD = false;
    private static final String TAG = "CalendarAppWidgetModel";
    final ListMultimap<Integer, ContactBirthday> mBirthDayInfos;
    final Context mContext;
    final List<DayInfo> mDayInfos;
    final List<EventInfo> mEventInfos;
    final int mMaxJulianDay;
    final long mNow;
    final List<RowInfo> mRowInfos;
    final String mTimezone;
    final int mTodayJulianDay;

    static class RowInfo {
        static final int TYPE_BIRTHDAY = 2;
        static final int TYPE_DAY = 0;
        static final int TYPE_MEETING = 1;
        final int mIndex;
        final int mType;

        RowInfo(int i, int i2) {
            this.mType = i;
            this.mIndex = i2;
        }
    }

    static class EventInfo {
        boolean allDay;
        String beginAmPm;
        String beginTime;
        int color;
        long end;
        String endAmPm;
        String endTime;
        long id;
        long instanceId;
        boolean isAlarmEvent;
        boolean isLunarEvent;
        String rrule;
        int selfAttendeeStatus;
        long start;
        String title;
        String where;

        public String toString() {
            return "EventInfo [title=" + this.title + ", id=" + this.id + ", when=, separatorVisibility=, dividerVisibility=, where=" + this.where + ", color=" + String.format("0x%x", Integer.valueOf(this.color)) + ", selfAttendeeStatus=" + this.selfAttendeeStatus + ", isLunarEvent" + this.isLunarEvent + ", isAlarmEvent" + this.isAlarmEvent + "]";
        }

        public int hashCode() {
            int i = this.allDay ? 1231 : 1237;
            long j = this.id;
            int i2 = (((i + 31) * 31) + ((int) (j ^ (j >>> 32)))) * 31;
            long j2 = this.end;
            int i3 = (i2 + ((int) (j2 ^ (j2 >>> 32)))) * 31;
            long j3 = this.start;
            int i4 = (i3 + ((int) (j3 ^ (j3 >>> 32)))) * 31;
            String str = this.title;
            int iHashCode = (i4 + (str == null ? 0 : str.hashCode())) * 31;
            String str2 = this.where;
            return ((((iHashCode + (str2 != null ? str2.hashCode() : 0)) * 31) + this.color) * 31) + this.selfAttendeeStatus;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            EventInfo eventInfo = (EventInfo) obj;
            if (this.id != eventInfo.id || this.allDay != eventInfo.allDay || this.end != eventInfo.end || this.start != eventInfo.start) {
                return false;
            }
            String str = this.title;
            if (str == null) {
                if (eventInfo.title != null) {
                    return false;
                }
            } else if (!str.equals(eventInfo.title)) {
                return false;
            }
            String str2 = this.where;
            if (str2 == null) {
                if (eventInfo.where != null) {
                    return false;
                }
            } else if (!str2.equals(eventInfo.where)) {
                return false;
            }
            return this.color == eventInfo.color && this.selfAttendeeStatus == eventInfo.selfAttendeeStatus && this.isLunarEvent == eventInfo.isLunarEvent && this.isAlarmEvent == eventInfo.isAlarmEvent;
        }
    }

    public static class DayInfo {
        public String holidays;
        public boolean isFreeDay;
        public final String mDateLabel;
        public final int mJulianDay;
        public final long mMilis;
        public final String mWeekDayLabel;
        public final int mYearDayMonth;

        public static int yearMonthDay(int i, int i2, int i3) {
            return (i * 1000) + (i2 * 100) + i3;
        }

        DayInfo(Context context, int i, int i2) {
            this.mYearDayMonth = i2;
            long julianDay = new SafeTime().setJulianDay(i);
            this.mMilis = julianDay;
            this.mJulianDay = i;
            this.mWeekDayLabel = Utils.formatDateRange(context, julianDay, julianDay, 2);
            this.mDateLabel = Utils.formatDateRange(context, julianDay, julianDay, 16);
            this.isFreeDay = FreeDayService.getInstance().isFreeDay(i);
            FreeDayService.getInstance().requestHolidayName(context, i, new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.widget.CalendarAppWidgetModel.DayInfo.1
                @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
                public void onResult(Object obj, Object obj2) {
                    String str = (String) obj;
                    if (TextUtils.isEmpty(str)) {
                        return;
                    }
                    DayInfo.this.holidays = ", " + str;
                }
            }, 1);
        }

        public String toString() {
            return this.mWeekDayLabel;
        }

        public int hashCode() {
            String str = this.mWeekDayLabel;
            return (((str == null ? 0 : str.hashCode()) + 31) * 31) + this.mJulianDay;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            DayInfo dayInfo = (DayInfo) obj;
            String str = this.mWeekDayLabel;
            if (str == null) {
                if (dayInfo.mWeekDayLabel != null) {
                    return false;
                }
            } else if (!str.equals(dayInfo.mWeekDayLabel)) {
                return false;
            }
            return this.mJulianDay == dayInfo.mJulianDay;
        }

        public static int yearMonthDay(Calendar calendar) {
            return yearMonthDay(calendar.get(1), calendar.get(2), calendar.get(5));
        }

        public static int yearMonthDayToMonthDay(int i) {
            return i % 1000;
        }

        public static int yearMonthDayToYear(int i) {
            return i / 1000;
        }
    }

    public CalendarAppWidgetModel(Context context, String str) {
        this.mTimezone = str;
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.mNow = jCurrentTimeMillis;
        Time time = new SafeTime(str);
        time.setToNow();
        int julianDay = Time.getJulianDay(jCurrentTimeMillis, time.gmtoff);
        this.mTodayJulianDay = julianDay;
        this.mMaxJulianDay = (julianDay + 3650) - 1;
        this.mEventInfos = new ArrayList();
        this.mRowInfos = new ArrayList();
        this.mDayInfos = new ArrayList();
        this.mBirthDayInfos = ArrayListMultimap.create();
        this.mContext = context;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void buildFromCursors(Cursor cursor, Cursor cursor2, String str) {
        String string;
        CalendarAppWidgetModel calendarAppWidgetModel = this;
        Cursor cursor3 = cursor;
        Cursor cursor4 = cursor2;
        Time time = new SafeTime(str);
        ArrayList arrayList = new ArrayList(3650);
        int i = 0;
        for (int i2 = 0; i2 < 3650; i2++) {
            arrayList.add(new LinkedList());
        }
        time.setToNow();
        cursor4.moveToPosition(-1);
        String timeZone = Utils.getTimeZone(calendarAppWidgetModel.mContext, null);
        CalendarAppWidgetModel calendarAppWidgetModel2 = calendarAppWidgetModel;
        while (cursor2.moveToNext()) {
            cursor2.getPosition();
            long j = cursor4.getLong(5);
            boolean i3 = cursor4.getInt(i) != 0;
            long jConvertAlldayUtcToLocal = cursor4.getLong(1);
            long jConvertAlldayUtcToLocal2 = cursor4.getLong(2);
            String string2 = cursor4.getString(3);
            String string3 = cursor4.getString(4);
            int i4 = cursor4.getInt(6);
            int i5 = cursor4.getInt(7);
            int i6 = cursor4.getInt(12);
            if (!(i6 != 0)) {
                i6 = cursor4.getInt(9);
            }
            int i7 = i6;
            String string4 = cursor4.getString(13);
            long j2 = cursor4.getLong(14);
            int i8 = cursor4.getInt(8);
            boolean z = cursor4.getInt(16) == 1;
            ArrayList arrayList2 = arrayList;
            boolean z2 = cursor4.getInt(15) == -1;
            if (i3) {
                jConvertAlldayUtcToLocal = Utils.convertAlldayUtcToLocal(time, jConvertAlldayUtcToLocal, timeZone);
                jConvertAlldayUtcToLocal2 = Utils.convertAlldayUtcToLocal(time, jConvertAlldayUtcToLocal2, timeZone);
            }
            long j3 = jConvertAlldayUtcToLocal2;
            if (j3 < calendarAppWidgetModel2.mNow) {
                arrayList = arrayList2;
            } else {
                String str2 = timeZone;
                int size = calendarAppWidgetModel2.mEventInfos.size();
                ArrayList arrayList3 = arrayList2;
                Time time2 = time;
                sortedAdd(populateEventInfo(j, i3, jConvertAlldayUtcToLocal, j3, i4, i5, string2, string3, i7, i8, z, string4, j2, z2), this.mEventInfos);
                int iMax = Math.max(i4, this.mTodayJulianDay);
                int iMin = Math.min(i5, this.mMaxJulianDay);
                while (iMax <= iMin) {
                    ArrayList arrayList4 = arrayList3;
                    LinkedList linkedList = (LinkedList) arrayList4.get(iMax - this.mTodayJulianDay);
                    int i9 = size;
                    RowInfo rowInfo = new RowInfo(1, i9);
                    if (i3) {
                        linkedList.addFirst(rowInfo);
                    } else {
                        linkedList.add(rowInfo);
                    }
                    iMax++;
                    arrayList3 = arrayList4;
                    size = i9;
                }
                cursor3 = cursor;
                cursor4 = cursor2;
                calendarAppWidgetModel2 = this;
                time = time2;
                timeZone = str2;
                arrayList = arrayList3;
            }
            i = 0;
            calendarAppWidgetModel2 = calendarAppWidgetModel2;
        }
        ArrayList arrayList5 = arrayList;
        Cursor cursor5 = cursor3;
        CalendarAppWidgetModel calendarAppWidgetModel3 = calendarAppWidgetModel2;
        if (cursor5 != null && cursor.moveToFirst()) {
            int columnIndexOrThrow = cursor5.getColumnIndexOrThrow("display_name");
            int columnIndexOrThrow2 = cursor5.getColumnIndexOrThrow("data1");
            int columnIndexOrThrow3 = cursor5.getColumnIndexOrThrow("contact_id");
            int columnIndexOrThrow4 = cursor5.getColumnIndexOrThrow("mimetype");
            int columnIndexOrThrow5 = cursor5.getColumnIndexOrThrow("data3");
            do {
                if (cursor5.getString(columnIndexOrThrow4).contains(ContactBirthdayLoaderBinder.BIRTHDAY_MIMETYPE_IDENTIFIER)) {
                    string = cursor5.getString(columnIndexOrThrow5);
                } else {
                    string = cursor5.getString(columnIndexOrThrow2);
                }
                ContactBirthday contactBirthday = new ContactBirthday(cursor5.getString(columnIndexOrThrow), string);
                contactBirthday.contactId = String.valueOf(cursor5.getInt(columnIndexOrThrow3));
                calendarAppWidgetModel3.mBirthDayInfos.put(Integer.valueOf(DayInfo.yearMonthDayToMonthDay(contactBirthday.getYearMonthDay())), contactBirthday);
            } while (cursor.moveToNext());
        }
        int i10 = calendarAppWidgetModel3.mTodayJulianDay;
        Calendar calendar = Calendar.getInstance();
        int size2 = 0;
        for (int i11 = 0; i11 < arrayList5.size(); i11++) {
            LinkedList linkedList2 = (LinkedList) arrayList5.get(i11);
            int iYearMonthDay = DayInfo.yearMonthDay(calendar);
            List<ContactBirthday> list = calendarAppWidgetModel3.mBirthDayInfos.get(Integer.valueOf(DayInfo.yearMonthDayToMonthDay(iYearMonthDay)));
            if (!linkedList2.isEmpty() || !list.isEmpty()) {
                DayInfo dayInfo = new DayInfo(calendarAppWidgetModel3.mContext, i10, iYearMonthDay);
                int size3 = calendarAppWidgetModel3.mDayInfos.size();
                calendarAppWidgetModel3.mDayInfos.add(dayInfo);
                if (i10 != calendarAppWidgetModel3.mTodayJulianDay) {
                    calendarAppWidgetModel3.mRowInfos.add(new RowInfo(0, size3));
                }
                if (!list.isEmpty()) {
                    calendarAppWidgetModel3.mRowInfos.add(new RowInfo(2, size3));
                    size2++;
                }
                calendarAppWidgetModel3.mRowInfos.addAll(linkedList2);
                size2 += linkedList2.size();
            }
            calendar.add(5, 1);
            i10++;
            size2++;
            if (size2 >= 2000) {
                return;
            }
        }
    }

    private void sortedAdd(EventInfo eventInfo, List<EventInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).start > eventInfo.start) {
                list.add(i, eventInfo);
                return;
            }
        }
        list.add(eventInfo);
    }

    private EventInfo populateEventInfo(long j, boolean z, long j2, long j3, int i, int i2, String str, String str2, int i3, int i4, boolean z2, String str3, long j4, boolean z3) {
        SimpleDateFormat simpleDateFormat;
        EventInfo eventInfo = new EventInfo();
        eventInfo.id = j;
        eventInfo.start = j2;
        eventInfo.end = j3;
        eventInfo.allDay = z;
        eventInfo.color = i3;
        eventInfo.selfAttendeeStatus = i4;
        eventInfo.isLunarEvent = z2;
        eventInfo.rrule = str3;
        eventInfo.instanceId = j4;
        eventInfo.isAlarmEvent = z3;
        if (TextUtils.isEmpty(str) && !z3) {
            eventInfo.title = this.mContext.getString(R.string.no_title_label);
        } else {
            eventInfo.title = str;
        }
        if (!TextUtils.isEmpty(str2)) {
            eventInfo.where = str2;
        }
        if (eventInfo.allDay) {
            eventInfo.beginTime = this.mContext.getResources().getString(R.string.calendar_agenda_all_day_event_txt);
        } else {
            Date date = new Date(eventInfo.start);
            Date date2 = new Date(eventInfo.end);
            if (DateFormat.is24HourFormat(this.mContext)) {
                simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            } else {
                simpleDateFormat = new SimpleDateFormat("a", Locale.getDefault());
                eventInfo.beginAmPm = simpleDateFormat.format(date);
                eventInfo.endAmPm = simpleDateFormat.format(date2);
                simpleDateFormat.applyLocalizedPattern("h:mm");
            }
            eventInfo.beginTime = simpleDateFormat.format(date);
            eventInfo.endTime = simpleDateFormat.format(date2);
        }
        return eventInfo;
    }

    public String toString() {
        return "\nCalendarAppWidgetModel [eventInfos=" + this.mEventInfos + "]";
    }
}
