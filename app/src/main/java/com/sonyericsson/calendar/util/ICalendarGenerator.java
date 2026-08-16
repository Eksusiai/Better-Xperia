package com.sonyericsson.calendar.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import com.sonymobile.calendar.R;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
public class ICalendarGenerator {
    private static final int ATTENDEES_INDEX_EMAIL = 0;
    private static final int ATTENDEES_INDEX_NAME = 1;
    private static final int ATTENDEES_INDEX_RELATIONSHIP = 2;
    private static final int ATTENDEE_INDEX_ATTENDEE_TYPE = 3;
    private static final int CALENDAR_INDEX_ACCOUNT_NAME = 1;
    static final long DAYS = 86400000;
    private static final int EVENT_INDEX_ALL_DAY = 2;
    private static final int EVENT_INDEX_CALENDAR_ID = 3;
    private static final int EVENT_INDEX_DESCRIPTION = 5;
    private static final int EVENT_INDEX_DTEND = 12;
    private static final int EVENT_INDEX_DTSTART = 4;
    private static final int EVENT_INDEX_DURATION = 8;
    private static final int EVENT_INDEX_LOCATION = 6;
    private static final int EVENT_INDEX_ORGANIZER = 7;
    private static final int EVENT_INDEX_ORIGINAL_INSTANCE_TIME = 11;
    private static final int EVENT_INDEX_ORIGINAL_SYNC_ID = 9;
    private static final int EVENT_INDEX_RRULE = 1;
    private static final int EVENT_INDEX_SYNC_DATA_2 = 10;
    private static final int EVENT_INDEX_SYNC_DATA_4 = 13;
    private static final int EVENT_INDEX_TITLE = 0;
    static final int HOURS = 3600000;
    private static final String ICALENDAR_ATTENDEE = "ATTENDEE";
    private static final String ICALENDAR_ATTENDEE_OPT = ";ROLE=OPT-PARTICIPANT";
    static final String ICALENDAR_ATTENDEE_ORGANIZER = "ORGANIZER";
    private static final String ICALENDAR_ATTENDEE_REQ = ";ROLE=REQ-PARTICIPANT";
    private static final String ICALENDAR_FORWARD_ATTENDEE = "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE";
    static final int MINUTES = 60000;
    private static final int OPT_PARTICIPANT = 2;
    private static final int REQ_PARTICIPANT = 1;
    static final int SECONDS = 1000;
    private SimpleIcsWriter icsWriter;
    private int mAction;
    private Cursor mAttendeesCursor;
    private long mBeginTime;
    private Cursor mCalendarsCursor;
    private Context mContext;
    private Cursor mEventCursor;
    private String[] mToList;
    private int mWhich;
    static final TimeZone sGmtTimeZone = TimeZone.getTimeZone("GMT");
    static final int sCurrentYear = new GregorianCalendar().get(1);
    static final String[] sTwoCharacterNumbers = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    static final String[] sDayTokens = {"SU", "MO", "TU", "WE", "TH", "FR", "SA"};

    public void setmToList(String[] strArr) {
        this.mToList = strArr;
    }

    public ICalendarGenerator(Context context, Cursor cursor, Cursor cursor2, Cursor cursor3) {
        this.icsWriter = null;
        this.icsWriter = new SimpleIcsWriter();
        this.mEventCursor = cursor;
        this.mCalendarsCursor = cursor2;
        this.mAttendeesCursor = cursor3;
        this.mContext = context;
    }

    public String buildMessageTextFromEntityValues(Context context, StringBuilder sb) {
        String str;
        int i;
        if (sb == null) {
            sb = new StringBuilder();
        }
        Resources resources = context.getResources();
        Date date = new Date(this.mEventCursor.getLong(4));
        boolean z = this.mEventCursor.getLong(2) == 1;
        boolean z2 = this.mEventCursor.getString(9) == null && this.mEventCursor.getString(1) != null;
        if (z) {
            str = DateFormat.getDateInstance().format(date);
            i = z2 ? R.string.meeting_allday_recurring : R.string.meeting_allday;
        } else {
            str = DateFormat.getDateTimeInstance().format(date);
            i = z2 ? R.string.meeting_recurring : R.string.meeting_when;
        }
        sb.append(resources.getString(i, str));
        String string = this.mEventCursor.getString(6);
        if (!TextUtils.isEmpty(string)) {
            sb.append("\n");
            sb.append(resources.getString(R.string.meeting_where, string));
        }
        String string2 = this.mEventCursor.getString(5);
        if (string2 != null) {
            sb.append("\n--\n");
            sb.append(string2);
        }
        return sb.toString();
    }

    public void setWhich(int i) {
        this.mWhich = i;
    }

    public void setDate(long j) {
        this.mBeginTime = j;
    }

    public void setAction(int i) {
        this.mAction = i;
    }

    public void fillWritter() {
        long millis;
        long millis2;
        String string;
        String string2;
        this.icsWriter.writeTag(CalendarConstants.BEGIN, CalendarConstants.VCALENDAR);
        this.icsWriter.writeTag(CalendarConstants.VERSION, CalendarConstants.ICALENDAR_DEFAULT_VERSION);
        this.icsWriter.writeTag(CalendarConstants.METHOD, "REQUEST");
        this.icsWriter.writeTag(CalendarConstants.PRODID, "AndroidEmail");
        TimeZone timeZone = sGmtTimeZone;
        boolean z = this.mEventCursor.getInt(2) != 0;
        String str = z ? ";VALUE=DATE" : "";
        String string3 = this.mEventCursor.getString(1);
        String string4 = this.mEventCursor.getString(9);
        if (!z && (string3 != null || string4 != null)) {
            TimeZone timeZone2 = TimeZone.getDefault();
            try {
                timeZoneToVTimezone(timeZone2, this.icsWriter);
            } catch (IOException e) {
                Log.e("tagd", e.getMessage());
            }
            str = ";TZID=" + timeZone2.getID();
            timeZone = timeZone2;
        }
        this.icsWriter.writeTag(CalendarConstants.BEGIN, CalendarConstants.VEVENT);
        String string5 = this.mEventCursor.getString(10);
        if (string5 == null) {
            string5 = UUID.randomUUID().toString();
        }
        this.icsWriter.writeTag(CalendarConstants.UID, string5);
        this.icsWriter.writeTag("DTSTAMP", millisToEasDateTime(System.currentTimeMillis()));
        long j = this.mEventCursor.getLong(4);
        if (j != 0) {
            if (this.mWhich == 0) {
                long j2 = this.mBeginTime;
                if (j2 != 0) {
                    j = j2;
                }
            }
            this.icsWriter.writeTag(CalendarConstants.DTSTART + str, millisToEasDateTime(j, timeZone, !z));
        }
        boolean z2 = string4 != null;
        if (z2) {
            this.icsWriter.writeTag("RECURRENCE-ID" + str, millisToEasDateTime(this.mEventCursor.getLong(11), timeZone, !z));
        }
        if (this.mEventCursor.getString(8) != null) {
            Duration duration = new Duration();
            try {
                duration.parse(this.mEventCursor.getString(8));
                millis = duration.getMillis();
            } catch (ParseException unused) {
                millis = 3600000;
            }
            this.icsWriter.writeTag(CalendarConstants.DTEND + str, millisToEasDateTime(j + millis, timeZone, !z));
        } else if (this.mEventCursor.getLong(12) != 0) {
            this.icsWriter.writeTag(CalendarConstants.DTEND + str, millisToEasDateTime(this.mEventCursor.getLong(12), timeZone, !z));
        }
        if (this.mEventCursor.getString(6) != null) {
            this.icsWriter.writeTag(CalendarConstants.LOCATION, this.mEventCursor.getString(6));
        }
        String string6 = this.mEventCursor.getString(13);
        if (string6 == null || string6.length() <= 0) {
            string6 = "0";
        }
        Resources resources = this.mContext.getResources();
        String string7 = this.mEventCursor.getString(0);
        this.icsWriter.writeTag(CalendarConstants.SUMMARY, string7 != null ? string7 : "");
        StringBuilder sb = new StringBuilder();
        if (z2) {
            sb.append(resources.getString(R.string.exception_updated, DateFormat.getDateInstance().format(new Date(this.mEventCursor.getLong(11)))));
            sb.append("\n\n");
        }
        String strBuildMessageTextFromEntityValues = buildMessageTextFromEntityValues(this.mContext, sb);
        if (strBuildMessageTextFromEntityValues.length() > 0) {
            this.icsWriter.writeTag(CalendarConstants.DESCRIPTION, strBuildMessageTextFromEntityValues);
        }
        Duration duration2 = new Duration();
        String string8 = this.mEventCursor.getString(8);
        if (string8 != null) {
            try {
                duration2.parse(string8);
                millis2 = duration2.getMillis();
            } catch (ParseException unused2) {
                millis2 = 0;
            }
        } else {
            millis2 = 0;
        }
        this.icsWriter.writeTag("X-MICROSOFT-CDO-ALLDAYEVENT", (this.mEventCursor.getInt(2) != 0) | ((millis2 > DAYS ? 1 : (millis2 == DAYS ? 0 : -1)) == 0) ? "TRUE" : "FALSE");
        String string9 = this.mEventCursor.getString(1);
        if (string9 != null && this.mWhich != 0) {
            this.icsWriter.writeTag(CalendarConstants.RRULE, string9);
        }
        this.mCalendarsCursor.moveToFirst();
        String string10 = this.mCalendarsCursor.getString(1);
        String str2 = "ORGANIZER";
        if (this.mAction == 0) {
            this.mAttendeesCursor.moveToFirst();
            while (true) {
                string = null;
                if (this.mAttendeesCursor.getInt(2) == 2) {
                    string = this.mAttendeesCursor.getString(1);
                    string2 = this.mAttendeesCursor.getString(0);
                    break;
                } else if (!this.mAttendeesCursor.moveToNext()) {
                    string2 = null;
                    break;
                }
            }
            if (string != null && string.length() > 0) {
                str2 = "ORGANIZER;CN=" + SimpleIcsWriter.quoteParamValue(string);
            }
            this.icsWriter.writeTag(str2 + ";SENT-BY=" + SimpleIcsWriter.quoteParamValue("MAILTO:" + string10), "MAILTO:" + string2);
            String[] strArr = this.mToList;
            String str3 = ICALENDAR_FORWARD_ATTENDEE;
            for (String str4 : strArr) {
                int iIndexOf = str4.indexOf(47);
                String strSubstring = str4.substring(0, iIndexOf);
                String strSubstring2 = str4.substring(iIndexOf + 1);
                if (strSubstring != null && strSubstring.length() > 0) {
                    str3 = str3 + ";CN=" + SimpleIcsWriter.quoteParamValue(strSubstring);
                }
                this.icsWriter.writeTag(str3, "MAILTO:" + strSubstring2);
            }
        } else {
            this.mAttendeesCursor.moveToFirst();
            do {
                if (this.mAttendeesCursor.getInt(2) == 2) {
                    String string11 = this.mAttendeesCursor.getString(1);
                    this.icsWriter.writeTag((string11 == null || string11.length() <= 0) ? "ORGANIZER" : "ORGANIZER;CN=" + SimpleIcsWriter.quoteParamValue(string11), "MAILTO:" + this.mAttendeesCursor.getString(0));
                } else {
                    addAttendeeToMessage(this.icsWriter, this.mAttendeesCursor.getString(1), this.mAttendeesCursor.getString(0), this.mAttendeesCursor.getInt(3), this.mAction);
                }
            } while (this.mAttendeesCursor.moveToNext());
        }
        this.icsWriter.writeTag("CLASS", "PUBLIC");
        this.icsWriter.writeTag(CalendarConstants.STATUS, "CONFIRMED");
        this.icsWriter.writeTag("TRANSP", "OPAQUE");
        this.icsWriter.writeTag(CalendarConstants.PRIORITY, "5");
        this.icsWriter.writeTag("SEQUENCE", string6);
        this.icsWriter.writeTag("END", CalendarConstants.VEVENT);
        this.icsWriter.writeTag("END", CalendarConstants.VCALENDAR);
    }

    private void addAttendeeToMessage(SimpleIcsWriter simpleIcsWriter, String str, String str2, int i, int i2) {
        if (i2 == 0) {
            String str3 = "ATTENDEE";
            if (i == 1) {
                str3 = "ATTENDEE" + ICALENDAR_ATTENDEE_REQ;
            } else if (i == 2) {
                str3 = "ATTENDEE" + ICALENDAR_ATTENDEE_OPT;
            }
            if (str != null) {
                str3 = str3 + ";CN=" + SimpleIcsWriter.quoteParamValue(str);
            }
            simpleIcsWriter.writeTag(str3 + ";RSVP=FALSE", "MAILTO:" + str2);
        }
    }

    static int getTrueTransitionHour(GregorianCalendar gregorianCalendar) {
        int i = gregorianCalendar.get(11) + 1;
        if (i == 24) {
            return 0;
        }
        return i;
    }

    static int getTrueTransitionMinute(GregorianCalendar gregorianCalendar) {
        int i = gregorianCalendar.get(12);
        if (i == 59) {
            return 0;
        }
        return i;
    }

    static String transitionMillisToVCalendarTime(long j, TimeZone timeZone, boolean z) {
        StringBuilder sb = new StringBuilder();
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.setTimeInMillis(j);
        sb.append(gregorianCalendar.get(1));
        sb.append(formatTwo(gregorianCalendar.get(2) + 1));
        sb.append(formatTwo(gregorianCalendar.get(5)));
        sb.append('T');
        sb.append(formatTwo(getTrueTransitionHour(gregorianCalendar)));
        sb.append(formatTwo(getTrueTransitionMinute(gregorianCalendar)));
        sb.append(formatTwo(0));
        return sb.toString();
    }

    static RRule inferRRuleFromCalendars(GregorianCalendar[] gregorianCalendarArr) {
        boolean z = false;
        GregorianCalendar gregorianCalendar = gregorianCalendarArr[0];
        if (gregorianCalendar == null) {
            return null;
        }
        int i = gregorianCalendar.get(2);
        int i2 = gregorianCalendar.get(5);
        int i3 = gregorianCalendar.get(7);
        int i4 = gregorianCalendar.get(8);
        int actualMaximum = gregorianCalendar.getActualMaximum(8);
        int i5 = i4;
        boolean z2 = false;
        for (int i6 = 1; i6 < gregorianCalendarArr.length; i6++) {
            GregorianCalendar gregorianCalendar2 = gregorianCalendarArr[i6];
            if (gregorianCalendar2 == null || gregorianCalendar2.get(2) != i) {
                return null;
            }
            if (i3 == gregorianCalendar2.get(7)) {
                if (z) {
                    return null;
                }
                int i7 = gregorianCalendar2.get(8);
                if (i5 != i7) {
                    if ((i5 >= 0 && i5 != actualMaximum) || i7 != gregorianCalendar2.getActualMaximum(8)) {
                        return null;
                    }
                    i5 = -1;
                }
                z2 = true;
            } else {
                if (i2 != gregorianCalendar2.get(5) || z2) {
                    return null;
                }
                z = true;
            }
        }
        if (z) {
            return new RRule(i + 1, i2);
        }
        return new RRule(i + 1, i3, i5);
    }

    private void timeZoneToVTimezone(TimeZone timeZone, SimpleIcsWriter simpleIcsWriter) throws IOException {
        int rawOffset = timeZone.getRawOffset() / MINUTES;
        String strUtcOffsetString = utcOffsetString(rawOffset);
        simpleIcsWriter.writeTag(CalendarConstants.BEGIN, "VTIMEZONE");
        simpleIcsWriter.writeTag(CalendarConstants.TZID, timeZone.getID());
        simpleIcsWriter.writeTag("X-LIC-LOCATION", timeZone.getDisplayName());
        if (!timeZone.useDaylightTime()) {
            writeNoDST(simpleIcsWriter, timeZone, strUtcOffsetString);
            return;
        }
        GregorianCalendar[] gregorianCalendarArr = new GregorianCalendar[3];
        GregorianCalendar[] gregorianCalendarArr2 = new GregorianCalendar[3];
        if (!getDSTCalendars(timeZone, gregorianCalendarArr, gregorianCalendarArr2)) {
            writeNoDST(simpleIcsWriter, timeZone, strUtcOffsetString);
            return;
        }
        RRule rRuleInferRRuleFromCalendars = inferRRuleFromCalendars(gregorianCalendarArr);
        RRule rRuleInferRRuleFromCalendars2 = inferRRuleFromCalendars(gregorianCalendarArr2);
        String strUtcOffsetString2 = utcOffsetString(rawOffset + (timeZone.getDSTSavings() / MINUTES));
        boolean z = (rRuleInferRRuleFromCalendars == null || rRuleInferRRuleFromCalendars2 == null) ? false : true;
        simpleIcsWriter.writeTag(CalendarConstants.BEGIN, CalendarConstants.DAYLIGHT);
        simpleIcsWriter.writeTag("TZOFFSETFROM", strUtcOffsetString);
        simpleIcsWriter.writeTag("TZOFFSETTO", strUtcOffsetString2);
        simpleIcsWriter.writeTag(CalendarConstants.DTSTART, transitionMillisToVCalendarTime(gregorianCalendarArr[0].getTimeInMillis(), timeZone, true));
        if (!z || this.mWhich == 0) {
            int i = 1;
            while (i < 3) {
                simpleIcsWriter.writeTag("RDATE", transitionMillisToVCalendarTime(gregorianCalendarArr[i].getTimeInMillis(), timeZone, true));
                i++;
                gregorianCalendarArr2 = gregorianCalendarArr2;
                gregorianCalendarArr = gregorianCalendarArr;
            }
        } else {
            simpleIcsWriter.writeTag(CalendarConstants.RRULE, rRuleInferRRuleFromCalendars.toString());
        }
        GregorianCalendar[] gregorianCalendarArr3 = gregorianCalendarArr2;
        simpleIcsWriter.writeTag("END", CalendarConstants.DAYLIGHT);
        simpleIcsWriter.writeTag(CalendarConstants.BEGIN, "STANDARD");
        simpleIcsWriter.writeTag("TZOFFSETFROM", strUtcOffsetString2);
        simpleIcsWriter.writeTag("TZOFFSETTO", strUtcOffsetString);
        simpleIcsWriter.writeTag(CalendarConstants.DTSTART, transitionMillisToVCalendarTime(gregorianCalendarArr3[0].getTimeInMillis(), timeZone, false));
        if (!z || this.mWhich == 0) {
            for (int i2 = 1; i2 < 3; i2++) {
                simpleIcsWriter.writeTag("RDATE", transitionMillisToVCalendarTime(gregorianCalendarArr3[i2].getTimeInMillis(), timeZone, true));
            }
        } else {
            simpleIcsWriter.writeTag(CalendarConstants.RRULE, rRuleInferRRuleFromCalendars2.toString());
        }
        simpleIcsWriter.writeTag("END", "STANDARD");
        simpleIcsWriter.writeTag("END", "VTIMEZONE");
    }

    static String utcOffsetString(int i) {
        StringBuilder sb = new StringBuilder();
        int i2 = i / 60;
        if (i2 < 0) {
            sb.append('-');
            i2 = 0 - i2;
        } else {
            sb.append('+');
        }
        int i3 = i % 60;
        if (i2 < 10) {
            sb.append('0');
        }
        sb.append(i2);
        if (i3 < 10) {
            sb.append('0');
        }
        sb.append(i3);
        return sb.toString();
    }

    private static void writeNoDST(SimpleIcsWriter simpleIcsWriter, TimeZone timeZone, String str) {
        simpleIcsWriter.writeTag(CalendarConstants.BEGIN, "STANDARD");
        simpleIcsWriter.writeTag("TZOFFSETFROM", str);
        simpleIcsWriter.writeTag("TZOFFSETTO", str);
        simpleIcsWriter.writeTag(CalendarConstants.DTSTART, millisToEasDateTime(0L));
        simpleIcsWriter.writeTag("END", "STANDARD");
        simpleIcsWriter.writeTag("END", "VTIMEZONE");
    }

    public static String millisToEasDateTime(long j) {
        return millisToEasDateTime(j, sGmtTimeZone, true);
    }

    public static String millisToEasDateTime(long j, TimeZone timeZone, boolean z) {
        StringBuilder sb = new StringBuilder();
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.setTimeInMillis(j);
        sb.append(gregorianCalendar.get(1));
        sb.append(formatTwo(gregorianCalendar.get(2) + 1));
        sb.append(formatTwo(gregorianCalendar.get(5)));
        if (z) {
            sb.append('T');
            sb.append(formatTwo(gregorianCalendar.get(11)));
            sb.append(formatTwo(gregorianCalendar.get(12)));
            sb.append(formatTwo(gregorianCalendar.get(13)));
            if (timeZone == sGmtTimeZone) {
                sb.append('Z');
            }
        }
        return sb.toString();
    }

    static String formatTwo(int i) {
        if (i <= 12) {
            return sTwoCharacterNumbers[i];
        }
        return Integer.toString(i);
    }

    static GregorianCalendar findTransitionDate(TimeZone timeZone, long j, long j2, boolean z) {
        long j3 = j2;
        while (j3 - j > 60000) {
            long j4 = ((j + j3) / 2) + 1;
            if (timeZone.inDaylightTime(new Date(j4)) != z) {
                j3 = j4;
            } else {
                j = j4;
            }
        }
        if (j3 == j2) {
            return null;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
        gregorianCalendar.setTimeInMillis(j);
        return gregorianCalendar;
    }

    static boolean getDSTCalendars(TimeZone timeZone, GregorianCalendar[] gregorianCalendarArr, GregorianCalendar[] gregorianCalendarArr2) {
        int length = gregorianCalendarArr.length;
        if (gregorianCalendarArr2.length != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone);
            gregorianCalendar.set(sCurrentYear + i, 0, 1, 0, 0, 0);
            long timeInMillis = gregorianCalendar.getTimeInMillis();
            long j = 31536000000L + timeInMillis + 21600000;
            boolean zInDaylightTime = timeZone.inDaylightTime(new Date(timeInMillis));
            GregorianCalendar gregorianCalendarFindTransitionDate = findTransitionDate(timeZone, timeInMillis, j, zInDaylightTime);
            if (gregorianCalendarFindTransitionDate == null) {
                return false;
            }
            if (zInDaylightTime) {
                gregorianCalendarArr2[i] = gregorianCalendarFindTransitionDate;
            } else {
                gregorianCalendarArr[i] = gregorianCalendarFindTransitionDate;
            }
            GregorianCalendar gregorianCalendarFindTransitionDate2 = findTransitionDate(timeZone, timeInMillis, j, !zInDaylightTime);
            if (gregorianCalendarFindTransitionDate2 == null) {
                return false;
            }
            if (zInDaylightTime) {
                gregorianCalendarArr[i] = gregorianCalendarFindTransitionDate2;
            } else {
                gregorianCalendarArr2[i] = gregorianCalendarFindTransitionDate2;
            }
        }
        return true;
    }

    public SimpleIcsWriter getWritter() {
        return this.icsWriter;
    }

    static class RRule {
        static final int RRULE_DATE = 2;
        static final int RRULE_DAY_WEEK = 1;
        static final int RRULE_NONE = 0;
        int date;
        int dayOfWeek;
        int month;
        int type = 2;
        int week;

        RRule(int i, int i2) {
            this.month = i;
            this.date = i2;
        }

        RRule(int i, int i2, int i3) {
            this.month = i;
            this.dayOfWeek = i2;
            this.week = i3;
        }

        public String toString() {
            if (this.type == 1) {
                return "FREQ=YEARLY;BYMONTH=" + this.month + ";BYDAY=" + this.week + ICalendarGenerator.sDayTokens[this.dayOfWeek - 1];
            }
            return "FREQ=YEARLY;BYMONTH=" + this.month + ";BYMONTHDAY=" + this.date;
        }
    }

    private static class Duration {
        public int days;
        public int hours;
        public int minutes;
        public int seconds;
        public int sign = 1;
        public int weeks;

        /* JADX WARN: Code duplicated, block: B:15:0x0032  */
        /* JADX WARN: Code duplicated, block: B:17:0x0036  */
        /* JADX WARN: Code duplicated, block: B:22:0x0048  */
        /* JADX WARN: Code duplicated, block: B:24:0x004c  */
        /* JADX WARN: Code duplicated, block: B:26:0x0050  */
        /* JADX WARN: Code duplicated, block: B:28:0x0054  */
        /* JADX WARN: Code duplicated, block: B:29:0x0057  */
        /* JADX WARN: Code duplicated, block: B:31:0x005b  */
        /* JADX WARN: Code duplicated, block: B:32:0x005e  */
        /* JADX WARN: Code duplicated, block: B:34:0x0062  */
        /* JADX WARN: Code duplicated, block: B:35:0x0065  */
        /* JADX WARN: Code duplicated, block: B:37:0x0069  */
        /* JADX WARN: Code duplicated, block: B:38:0x006c  */
        /* JADX WARN: Code duplicated, block: B:44:0x009f  */
        /* JADX WARN: Code duplicated, block: B:47:0x0073 A[SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:50:0x0070 A[SYNTHETIC] */
        public void parse(String str) throws ParseException {
            this.sign = 1;
            this.weeks = 0;
            this.days = 0;
            this.hours = 0;
            this.minutes = 0;
            this.seconds = 0;
            int length = str.length();
            if (length < 1) {
                return;
            }
            int i = 0;
            char cCharAt = str.charAt(0);
            if (cCharAt == '-') {
                this.sign = -1;
                i = 1;
            } else if (cCharAt == '+') {
                i = 1;
            }
            if (i >= length || str.charAt(i) != 'P') {
                throw new ParseException("Duration.parse(str='" + str + "') expected 'P' at index=" + i, i);
            }
            i++;
            int i3 = 0;
            while (i < length) {
                char cCharAt2 = str.charAt(i);
                if (cCharAt2 >= '0' && cCharAt2 <= '9') {
                    i3 = (i3 * 10) + (cCharAt2 - '0');
                } else if (cCharAt2 == 'W') {
                    this.weeks = i3;
                    i3 = 0;
                } else if (cCharAt2 == 'H') {
                    this.hours = i3;
                    i3 = 0;
                } else if (cCharAt2 == 'M') {
                    this.minutes = i3;
                    i3 = 0;
                } else if (cCharAt2 == 'S') {
                    this.seconds = i3;
                    i3 = 0;
                } else if (cCharAt2 == 'D') {
                    this.days = i3;
                    i3 = 0;
                } else if (cCharAt2 != 'T') {
                    throw new ParseException("Duration.parse(str='" + str + "') unexpected char '" + cCharAt2 + "' at index=" + i, i);
                }
                i++;
            }
        }

        public long getMillis() {
            return ((long) this.sign) * 1000 * ((long) ((this.weeks * 604800) + (this.days * 86400) + (this.hours * 3600) + (this.minutes * 60) + this.seconds));
        }
    }
}
