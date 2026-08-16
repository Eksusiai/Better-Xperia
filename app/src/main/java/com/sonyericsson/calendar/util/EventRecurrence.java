package com.sonyericsson.calendar.util;

import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import java.util.Calendar;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class EventRecurrence {
    private static final boolean ALLOW_LOWER_CASE = false;
    public static final int DAILY = 4;
    public static final int FR = 2097152;
    public static final int HOURLY = 3;
    public static final int MINUTELY = 2;
    public static final int MO = 131072;
    public static final int MONTHLY = 6;
    private static final boolean ONLY_ONE_UNTIL_COUNT = false;
    private static final int PARSED_BYDAY = 128;
    private static final int PARSED_BYHOUR = 64;
    private static final int PARSED_BYMINUTE = 32;
    private static final int PARSED_BYMONTH = 2048;
    private static final int PARSED_BYMONTHDAY = 256;
    private static final int PARSED_BYSECOND = 16;
    private static final int PARSED_BYSETPOS = 4096;
    private static final int PARSED_BYWEEKNO = 1024;
    private static final int PARSED_BYYEARDAY = 512;
    private static final int PARSED_COUNT = 4;
    private static final int PARSED_FREQ = 1;
    private static final int PARSED_INTERVAL = 8;
    private static final int PARSED_UNTIL = 2;
    private static final int PARSED_WKST = 8192;
    public static final int SA = 4194304;
    public static final int SECONDLY = 1;
    public static final int SU = 65536;
    private static final String TAG = "EventRecur";
    public static final int TH = 1048576;
    public static final int TU = 262144;
    private static final boolean VALIDATE_UNTIL = false;
    public static final int WE = 524288;
    public static final int WEEKLY = 5;
    public static final int YEARLY = 7;
    private static final HashMap<String, Integer> sParseFreqMap;
    private static HashMap<String, PartParser> sParsePartMap;
    private static final HashMap<String, Integer> sParseWeekdayMap;
    public int[] byday;
    public int bydayCount;
    public int[] bydayNum;
    public int[] byhour;
    public int byhourCount;
    public int[] byminute;
    public int byminuteCount;
    public int[] bymonth;
    public int bymonthCount;
    public int[] bymonthday;
    public int bymonthdayCount;
    public int[] bysecond;
    public int bysecondCount;
    public int[] bysetpos;
    public int bysetposCount;
    public int[] byweekno;
    public int byweeknoCount;
    public int[] byyearday;
    public int byyeardayCount;
    public int count;
    public int freq;
    public int interval;
    public Time startDate;
    public String until;
    public int wkst;

    static {
        HashMap<String, PartParser> map = new HashMap<>();
        sParsePartMap = map;
        map.put(RecurrenceRuleParser.FREQ, new ParseFreq());
        sParsePartMap.put(RecurrenceRuleParser.UNTIL, new ParseUntil());
        sParsePartMap.put(RecurrenceRuleParser.COUNT, new ParseCount());
        sParsePartMap.put(RecurrenceRuleParser.INTERVAL, new ParseInterval());
        sParsePartMap.put("BYSECOND", new ParseBySecond());
        sParsePartMap.put(RecurrenceRuleParser.BYMINUTE, new ParseByMinute());
        sParsePartMap.put(RecurrenceRuleParser.BYHOUR, new ParseByHour());
        sParsePartMap.put(RecurrenceRuleParser.BYDAY, new ParseByDay());
        sParsePartMap.put(RecurrenceRuleParser.BYMONTHDAY, new ParseByMonthDay());
        sParsePartMap.put(RecurrenceRuleParser.BYYEARDAY, new ParseByYearDay());
        sParsePartMap.put("BYWEEKNO", new ParseByWeekNo());
        sParsePartMap.put(RecurrenceRuleParser.BYMONTH, new ParseByMonth());
        sParsePartMap.put(RecurrenceRuleParser.BYSETPOS, new ParseBySetPos());
        sParsePartMap.put("WKST", new ParseWkst());
        HashMap<String, Integer> map2 = new HashMap<>();
        sParseFreqMap = map2;
        map2.put("SECONDLY", 1);
        map2.put(RecurrenceRuleParser.MINUTELY, 2);
        map2.put("HOURLY", 3);
        map2.put(RecurrenceRuleParser.DAILY, 4);
        map2.put(RecurrenceRuleParser.WEEKLY, 5);
        map2.put(RecurrenceRuleParser.MONTHLY, 6);
        map2.put(RecurrenceRuleParser.YEARLY, 7);
        HashMap<String, Integer> map3 = new HashMap<>();
        sParseWeekdayMap = map3;
        map3.put("SU", 65536);
        map3.put("MO", 131072);
        map3.put("TU", 262144);
        map3.put("WE", 524288);
        map3.put("TH", 1048576);
        map3.put("FR", 2097152);
        map3.put("SA", 4194304);
    }

    public static class InvalidFormatException extends RuntimeException {
        InvalidFormatException(String str) {
            super(str);
        }
    }

    public void setStartDate(Time time) {
        this.startDate = time;
    }

    public static int calendarDay2Day(int i) {
        switch (i) {
            case 1:
                return 65536;
            case 2:
                return 131072;
            case 3:
                return 262144;
            case 4:
                return 524288;
            case 5:
                return 1048576;
            case 6:
                return 2097152;
            case 7:
                return 4194304;
            default:
                throw new RuntimeException("bad day of week: " + i);
        }
    }

    public static int timeDay2Day(int i) {
        switch (i) {
            case 0:
                return 65536;
            case 1:
                return 131072;
            case 2:
                return 262144;
            case 3:
                return 524288;
            case 4:
                return 1048576;
            case 5:
                return 2097152;
            case 6:
                return 4194304;
            default:
                throw new RuntimeException("bad day of week: " + i);
        }
    }

    public static int day2CalendarDay(int i) {
        if (i == 65536) {
            return 1;
        }
        if (i == 131072) {
            return 2;
        }
        if (i == 262144) {
            return 3;
        }
        if (i == 524288) {
            return 4;
        }
        if (i == 1048576) {
            return 5;
        }
        if (i == 2097152) {
            return 6;
        }
        if (i == 4194304) {
            return 7;
        }
        throw new RuntimeException("bad day of week: " + i);
    }

    public static int getFirstByDayOrDefault(int[] iArr, Calendar calendar) {
        return (iArr == null || iArr.length <= 0) ? calendarDay2Day(calendar.get(7)) : day2CalendarDay(iArr[0]);
    }

    public static int getFirstByDayNumOrDefault(int[] iArr, Calendar calendar) {
        return (iArr == null || iArr.length <= 0) ? calendar.get(5) : iArr[0];
    }

    public static int getFirstByMonthOrDefault(int[] iArr, Calendar calendar) {
        return (iArr == null || iArr.length <= 0) ? calendar.get(2) : iArr[0] - 1;
    }

    private static String day2String(int i) {
        if (i == 65536) {
            return "SU";
        }
        if (i == 131072) {
            return "MO";
        }
        if (i == 262144) {
            return "TU";
        }
        if (i == 524288) {
            return "WE";
        }
        if (i == 1048576) {
            return "TH";
        }
        if (i == 2097152) {
            return "FR";
        }
        if (i == 4194304) {
            return "SA";
        }
        throw new IllegalArgumentException("bad day argument: " + i);
    }

    private static void appendNumbers(StringBuilder sb, String str, int i, int[] iArr) {
        if (i > 0) {
            sb.append(str);
            int i2 = i - 1;
            for (int i3 = 0; i3 < i2; i3++) {
                sb.append(iArr[i3]);
                sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
            }
            sb.append(iArr[i2]);
        }
    }

    private void appendByDay(StringBuilder sb, int i) {
        int i2 = this.bydayNum[i];
        if (i2 != 0) {
            sb.append(i2);
        }
        sb.append(day2String(this.byday[i]));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FREQ=");
        switch (this.freq) {
            case 1:
                sb.append("SECONDLY");
                break;
            case 2:
                sb.append(RecurrenceRuleParser.MINUTELY);
                break;
            case 3:
                sb.append("HOURLY");
                break;
            case 4:
                sb.append(RecurrenceRuleParser.DAILY);
                break;
            case 5:
                sb.append(RecurrenceRuleParser.WEEKLY);
                break;
            case 6:
                sb.append(RecurrenceRuleParser.MONTHLY);
                break;
            case 7:
                sb.append(RecurrenceRuleParser.YEARLY);
                break;
        }
        if (!TextUtils.isEmpty(this.until)) {
            sb.append(";UNTIL=");
            sb.append(this.until);
        } else if (this.count != 0) {
            sb.append(";COUNT=");
            sb.append(this.count);
        }
        if (this.interval != 0) {
            sb.append(";INTERVAL=");
            sb.append(this.interval);
        }
        if (this.wkst != 0) {
            sb.append(";WKST=");
            sb.append(day2String(this.wkst));
        }
        appendNumbers(sb, ";BYSECOND=", this.bysecondCount, this.bysecond);
        appendNumbers(sb, ";BYMINUTE=", this.byminuteCount, this.byminute);
        appendNumbers(sb, ";BYSECOND=", this.byhourCount, this.byhour);
        int i = this.bydayCount;
        if (i > 0) {
            sb.append(";BYDAY=");
            int i2 = i - 1;
            for (int i3 = 0; i3 < i2; i3++) {
                appendByDay(sb, i3);
                sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
            }
            appendByDay(sb, i2);
        }
        appendNumbers(sb, ";BYMONTHDAY=", this.bymonthdayCount, this.bymonthday);
        appendNumbers(sb, ";BYYEARDAY=", this.byyeardayCount, this.byyearday);
        appendNumbers(sb, ";BYWEEKNO=", this.byweeknoCount, this.byweekno);
        appendNumbers(sb, ";BYMONTH=", this.bymonthCount, this.bymonth);
        appendNumbers(sb, ";BYSETPOS=", this.bysetposCount, this.bysetpos);
        return sb.toString();
    }

    public boolean repeatsOnEveryWeekDay(int[] iArr) {
        int i;
        if (this.freq != 5 || (i = this.bydayCount) != iArr.length) {
            return false;
        }
        for (int i2 = 0; i2 < i; i2++) {
            if (!exists(this.byday, timeDay2Day(iArr[i2]))) {
                return false;
            }
        }
        return true;
    }

    private static boolean exists(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public boolean repeatsOnEveryWeekEnd(String[] strArr) {
        if (this.freq != 5 || strArr.length != this.bydayCount) {
            return false;
        }
        for (String str : strArr) {
            if (!exists(this.byday, timeDay2Day(Integer.parseInt(str)))) {
                return false;
            }
        }
        return true;
    }

    public boolean repeatsBiweekly() {
        return this.freq == 5 && this.interval == 2;
    }

    public boolean repeatsMonthlyOnDayCount() {
        return this.freq == 6 && this.bydayCount == 1 && this.bymonthdayCount == 0 && this.bydayNum[0] > 0;
    }

    private static boolean arraysEqual(int[] iArr, int i, int[] iArr2, int i2) {
        if (i != i2) {
            return false;
        }
        for (int i3 = 0; i3 < i; i3++) {
            if (iArr[i3] != iArr2[i3]) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        String str;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EventRecurrence)) {
            return false;
        }
        EventRecurrence eventRecurrence = (EventRecurrence) obj;
        Time time = this.startDate;
        if (time != null ? Time.compare(time, eventRecurrence.startDate) == 0 : eventRecurrence.startDate == null) {
            if (this.freq == eventRecurrence.freq && ((str = this.until) != null ? str.equals(eventRecurrence.until) : eventRecurrence.until == null) && this.count == eventRecurrence.count && this.interval == eventRecurrence.interval && this.wkst == eventRecurrence.wkst && arraysEqual(this.bysecond, this.bysecondCount, eventRecurrence.bysecond, eventRecurrence.bysecondCount) && arraysEqual(this.byminute, this.byminuteCount, eventRecurrence.byminute, eventRecurrence.byminuteCount) && arraysEqual(this.byhour, this.byhourCount, eventRecurrence.byhour, eventRecurrence.byhourCount) && arraysEqual(this.byday, this.bydayCount, eventRecurrence.byday, eventRecurrence.bydayCount) && arraysEqual(this.bydayNum, this.bydayCount, eventRecurrence.bydayNum, eventRecurrence.bydayCount) && arraysEqual(this.bymonthday, this.bymonthdayCount, eventRecurrence.bymonthday, eventRecurrence.bymonthdayCount) && arraysEqual(this.byyearday, this.byyeardayCount, eventRecurrence.byyearday, eventRecurrence.byyeardayCount) && arraysEqual(this.byweekno, this.byweeknoCount, eventRecurrence.byweekno, eventRecurrence.byweeknoCount) && arraysEqual(this.bymonth, this.bymonthCount, eventRecurrence.bymonth, eventRecurrence.bymonthCount) && arraysEqual(this.bysetpos, this.bysetposCount, eventRecurrence.bysetpos, eventRecurrence.bysetposCount)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    private void resetFields() {
        this.until = null;
        this.freq = 0;
        this.count = 0;
        this.interval = 0;
        this.bysecondCount = 0;
        this.byminuteCount = 0;
        this.byhourCount = 0;
        this.bydayCount = 0;
        this.bymonthdayCount = 0;
        this.byyeardayCount = 0;
        this.byweeknoCount = 0;
        this.bymonthCount = 0;
        this.bysetposCount = 0;
    }

    public void parse(String str) {
        resetFields();
        int i = 0;
        for (String str2 : str.split(";")) {
            if (!TextUtils.isEmpty(str2)) {
                int iIndexOf = str2.indexOf(61);
                if (iIndexOf <= 0) {
                    throw new InvalidFormatException("Missing LHS in " + str2);
                }
                String strSubstring = str2.substring(0, iIndexOf);
                String strSubstring2 = str2.substring(iIndexOf + 1);
                if (strSubstring2.length() == 0) {
                    throw new InvalidFormatException("Missing RHS in " + str2);
                }
                PartParser partParser = sParsePartMap.get(strSubstring);
                if (partParser == null) {
                    if (!strSubstring.startsWith("X-")) {
                        throw new InvalidFormatException("Couldn't find parser for " + strSubstring);
                    }
                } else {
                    int part = partParser.parsePart(strSubstring2, this);
                    if ((i & part) != 0) {
                        throw new InvalidFormatException("Part " + strSubstring + " was specified twice");
                    }
                    i |= part;
                }
            }
        }
        if ((i & 8192) == 0) {
            this.wkst = 131072;
        }
        if ((i & 1) == 0) {
            throw new InvalidFormatException("Must specify a FREQ value");
        }
        if ((i & 6) == 6) {
            Log.w(TAG, "Warning: rrule has both UNTIL and COUNT: " + str);
        }
    }

    static abstract class PartParser {
        public abstract int parsePart(String str, EventRecurrence eventRecurrence);

        PartParser() {
        }

        public static int parseIntRange(String str, int i, int i2, boolean z) {
            try {
                if (str.charAt(0) == '+') {
                    str = str.substring(1);
                }
                int i3 = Integer.parseInt(str);
                if (i3 < i || i3 > i2 || (i3 == 0 && !z)) {
                    throw new InvalidFormatException("Integer value out of range: " + str);
                }
                return i3;
            } catch (NumberFormatException unused) {
                throw new InvalidFormatException("Invalid integer value: " + str);
            }
        }

        public static int[] parseNumberList(String str, int i, int i2, boolean z) {
            if (!str.contains(RecurrenceRuleParser.VALUE_SEPARATOR)) {
                return new int[]{parseIntRange(str, i, i2, z)};
            }
            String[] strArrSplit = str.split(RecurrenceRuleParser.VALUE_SEPARATOR);
            int length = strArrSplit.length;
            int[] iArr = new int[length];
            for (int i3 = 0; i3 < length; i3++) {
                iArr[i3] = parseIntRange(strArrSplit[i3], i, i2, z);
            }
            return iArr;
        }
    }

    private static class ParseFreq extends PartParser {
        private ParseFreq() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            Integer num = (Integer) EventRecurrence.sParseFreqMap.get(str);
            if (num == null) {
                throw new InvalidFormatException("Invalid FREQ value: " + str);
            }
            eventRecurrence.freq = num.intValue();
            return 1;
        }
    }

    private static class ParseUntil extends PartParser {
        private ParseUntil() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            eventRecurrence.until = str;
            return 2;
        }
    }

    private static class ParseCount extends PartParser {
        private ParseCount() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            eventRecurrence.count = parseIntRange(str, 0, Integer.MAX_VALUE, true);
            return 4;
        }
    }

    private static class ParseInterval extends PartParser {
        private ParseInterval() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            eventRecurrence.interval = parseIntRange(str, 1, Integer.MAX_VALUE, false);
            return 8;
        }
    }

    private static class ParseBySecond extends PartParser {
        private ParseBySecond() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, 0, 59, true);
            eventRecurrence.bysecond = numberList;
            eventRecurrence.bysecondCount = numberList.length;
            return 16;
        }
    }

    private static class ParseByMinute extends PartParser {
        private ParseByMinute() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, 0, 59, true);
            eventRecurrence.byminute = numberList;
            eventRecurrence.byminuteCount = numberList.length;
            return 32;
        }
    }

    private static class ParseByHour extends PartParser {
        private ParseByHour() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, 0, 23, true);
            eventRecurrence.byhour = numberList;
            eventRecurrence.byhourCount = numberList.length;
            return 64;
        }
    }

    private static class ParseByDay extends PartParser {
        private ParseByDay() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] iArr;
            int[] iArr2;
            int length = 1;
            if (!str.contains(RecurrenceRuleParser.VALUE_SEPARATOR)) {
                iArr = new int[1];
                iArr2 = new int[1];
                parseWday(str, iArr, iArr2, 0);
            } else {
                String[] strArrSplit = str.split(RecurrenceRuleParser.VALUE_SEPARATOR);
                length = strArrSplit.length;
                iArr = new int[length];
                iArr2 = new int[length];
                for (int i = 0; i < length; i++) {
                    parseWday(strArrSplit[i], iArr, iArr2, i);
                }
            }
            eventRecurrence.byday = iArr;
            eventRecurrence.bydayNum = iArr2;
            eventRecurrence.bydayCount = length;
            return 128;
        }

        private static void parseWday(String str, int[] iArr, int[] iArr2, int i) {
            String strSubstring;
            int length = str.length() - 2;
            if (length > 0) {
                iArr2[i] = parseIntRange(str.substring(0, length), -53, 53, false);
                strSubstring = str.substring(length);
            } else {
                strSubstring = str;
            }
            Integer num = (Integer) EventRecurrence.sParseWeekdayMap.get(strSubstring);
            if (num == null) {
                throw new InvalidFormatException("Invalid BYDAY value: " + str);
            }
            iArr[i] = num.intValue();
        }
    }

    private static class ParseByMonthDay extends PartParser {
        private ParseByMonthDay() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, -31, 31, false);
            eventRecurrence.bymonthday = numberList;
            eventRecurrence.bymonthdayCount = numberList.length;
            return 256;
        }
    }

    private static class ParseByYearDay extends PartParser {
        private ParseByYearDay() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, -366, 366, false);
            eventRecurrence.byyearday = numberList;
            eventRecurrence.byyeardayCount = numberList.length;
            return 512;
        }
    }

    private static class ParseByWeekNo extends PartParser {
        private ParseByWeekNo() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, -53, 53, false);
            eventRecurrence.byweekno = numberList;
            eventRecurrence.byweeknoCount = numberList.length;
            return 1024;
        }
    }

    private static class ParseByMonth extends PartParser {
        private ParseByMonth() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, 1, 12, false);
            eventRecurrence.bymonth = numberList;
            eventRecurrence.bymonthCount = numberList.length;
            return 2048;
        }
    }

    private static class ParseBySetPos extends PartParser {
        private ParseBySetPos() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            int[] numberList = parseNumberList(str, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
            eventRecurrence.bysetpos = numberList;
            eventRecurrence.bysetposCount = numberList.length;
            return 4096;
        }
    }

    private static class ParseWkst extends PartParser {
        private ParseWkst() {
        }

        @Override // com.sonyericsson.calendar.util.EventRecurrence.PartParser
        public int parsePart(String str, EventRecurrence eventRecurrence) {
            Integer num = (Integer) EventRecurrence.sParseWeekdayMap.get(str);
            if (num == null) {
                throw new InvalidFormatException("Invalid WKST value: " + str);
            }
            eventRecurrence.wkst = num.intValue();
            return 8192;
        }
    }
}
