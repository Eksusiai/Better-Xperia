package com.sonyericsson.calendar.util;

import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class RecurrenceRuleParser {
    public static final String BYDAY = "BYDAY";
    public static final String BYHOUR = "BYHOUR";
    public static final String BYMINUTE = "BYMINUTE";
    public static final String BYMONTH = "BYMONTH";
    public static final String BYMONTHDAY = "BYMONTHDAY";
    public static final String BYSETPOS = "BYSETPOS";
    public static final String BYYEARDAY = "BYYEARDAY";
    public static final String COUNT = "COUNT";
    public static final String COUNT_DELIMITER = "#";
    public static final String DAILY = "DAILY";
    public static final String FREQ = "FREQ";
    public static final String INTERVAL = "INTERVAL";
    public static final String MINUTELY = "MINUTELY";
    public static final String MONTHLY = "MONTHLY";
    public static final String PATTERN_NO_NUMBER = "[0-9.]";
    public static final String PATTERN_NUMBER = "[^0-9.]";
    public static final String PROPERTY_SPLIT = ";";
    public static final String REPEAT_DAILY = "D";
    public static final String REPEAT_MINUTELY = "M";
    public static final String REPEAT_MONTHLY_DAY = "MD";
    public static final String REPEAT_MONTHLY_POS = "MP";
    public static final String REPEAT_WEEKLY = "W";
    public static final String REPEAT_YEARLY_DAY = "YD";
    public static final String REPEAT_YEARLY_MONTH = "YM";
    private static final Pattern RRULE_TAGS_RFC2445 = Pattern.compile("^(FREQ|UNTIL|COUNT|INTERVAL|BYSECOND|BYMINUTE|BYHOUR|BYDAY|BYMONTHDAY|BYYEARDAY|BYWEEKDAY|BYWEEKNO|BYMONTH|BYSETPOS|WKST|X-[A-Z0-9\\-]+)=(.*)", 2);
    public static final String UNTIL = "UNTIL";
    public static final String UNTIL_END_VALUE = "Z";
    public static final String VALUE_SEPARATOR = ",";
    public static final String VALUE_SPLIT = "=";
    public static final String VCAL_SPLIT = " ";
    public static final String WEEKLY = "WEEKLY";
    public static final String YEARLY = "YEARLY";

    public static String parseRruleToVCal(String str) throws IllegalArgumentException {
        return new VCalRrule(new RFC2445RRule(str)).toString();
    }

    public static String parseRruleToRFC2445(String str) throws IllegalArgumentException {
        return RRULE_TAGS_RFC2445.matcher(str).matches() ? str : new RFC2445RRule(new VCalRrule(str)).toString();
    }
}
