package com.sonyericsson.calendar.util;

import android.text.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class RFC2445RRule {
    public String byHrList;
    public String byMinList;
    public String byMoDayList;
    public String byMoList;
    public String bySpList;
    public String byWDayList;
    public String byYrDayList;
    public String count;
    public String frequency;
    public String interval;
    public String until;

    public RFC2445RRule(VCalRrule vCalRrule) {
        parseRules(vCalRrule);
        parseTimeRules(vCalRrule);
        if (TextUtils.isEmpty(vCalRrule.until)) {
            return;
        }
        this.until = vCalRrule.until;
    }

    public RFC2445RRule(String str) {
        splitTags(str);
    }

    private void splitTags(String str) {
        for (String str2 : str.split(";")) {
            String[] strArrSplit = str2.split("=");
            checkTagAndValue(strArrSplit[0], strArrSplit[1]);
        }
    }

    private void checkTagAndValue(String str, String str2) {
        str.hashCode();
        switch (str) {
            case "BYMONTHDAY":
                this.byMoDayList = str2;
                break;
            case "BYMINUTE":
                this.byMinList = str2;
                break;
            case "BYSETPOS":
                this.bySpList = str2;
                break;
            case "FREQ":
                this.frequency = str2;
                break;
            case "BYDAY":
                this.byWDayList = str2;
                break;
            case "COUNT":
                this.count = str2;
                break;
            case "UNTIL":
                this.until = str2;
                break;
            case "BYYEARDAY":
                this.byYrDayList = str2;
                break;
            case "BYMONTH":
                this.byMoList = str2;
                break;
            case "INTERVAL":
                this.interval = str2;
                break;
            case "BYHOUR":
                this.byHrList = str2;
                break;
        }
    }

    public String toString() {
        StringBuilder sbAppend = new StringBuilder(RecurrenceRuleParser.FREQ).append("=").append(this.frequency).append(";");
        if (this.until != null) {
            sbAppend.append(RecurrenceRuleParser.UNTIL).append("=").append(this.until).append(";");
        } else if (this.count != null) {
            sbAppend.append(RecurrenceRuleParser.COUNT).append("=").append(this.count).append(";");
        }
        if (this.interval != null) {
            sbAppend.append(RecurrenceRuleParser.INTERVAL).append("=").append(this.interval).append(";");
        }
        if (this.byMinList != null) {
            sbAppend.append(RecurrenceRuleParser.BYMINUTE).append("=").append(this.byMinList).append(";");
        }
        if (this.byHrList != null) {
            sbAppend.append(RecurrenceRuleParser.BYHOUR).append("=").append(this.byHrList).append(";");
        }
        if (this.byWDayList != null) {
            sbAppend.append(RecurrenceRuleParser.BYDAY).append("=").append(this.byWDayList).append(";");
        }
        if (this.byMoDayList != null) {
            sbAppend.append(RecurrenceRuleParser.BYMONTHDAY).append("=").append(this.byMoDayList).append(";");
        }
        if (this.byYrDayList != null) {
            sbAppend.append(RecurrenceRuleParser.BYYEARDAY).append("=").append(this.byYrDayList).append(";");
        }
        if (this.byMoList != null) {
            sbAppend.append(RecurrenceRuleParser.BYMONTH).append("=").append(this.byMoList).append(";");
        }
        if (this.bySpList != null) {
            sbAppend.append(RecurrenceRuleParser.BYSETPOS).append("=").append(this.bySpList).append(";");
        }
        sbAppend.setLength(sbAppend.length() - 1);
        return sbAppend.toString();
    }

    private void parseRules(VCalRrule vCalRrule) {
        String str;
        String str2 = null;
        if (!TextUtils.isEmpty(vCalRrule.yearly.rule)) {
            this.frequency = RecurrenceRuleParser.YEARLY;
            str2 = vCalRrule.yearly.value;
            str = vCalRrule.yearly.count;
        } else if (!TextUtils.isEmpty(vCalRrule.monthly.rule)) {
            this.frequency = RecurrenceRuleParser.MONTHLY;
            str2 = vCalRrule.monthly.value;
            str = vCalRrule.monthly.count;
        } else if (!TextUtils.isEmpty(vCalRrule.weekly.rule)) {
            this.frequency = RecurrenceRuleParser.WEEKLY;
            str2 = vCalRrule.weekly.value;
            str = vCalRrule.weekly.count;
        } else if (!TextUtils.isEmpty(vCalRrule.daily.rule)) {
            this.frequency = RecurrenceRuleParser.DAILY;
            str2 = vCalRrule.daily.value;
            str = vCalRrule.daily.count;
        } else if (TextUtils.isEmpty(vCalRrule.minutely.rule)) {
            str = null;
        } else {
            this.frequency = RecurrenceRuleParser.MINUTELY;
            str2 = vCalRrule.minutely.value;
            str = vCalRrule.minutely.count;
        }
        if (str2 != null) {
            this.interval = str2;
        }
        if (str != null) {
            this.count = str.replace(RecurrenceRuleParser.COUNT_DELIMITER, "");
        }
    }

    private void parseTimeRules(VCalRrule vCalRrule) {
        parseYearlyRule(vCalRrule);
        parseMonthlyRule(vCalRrule);
        parseWeeklyRule(vCalRrule);
        parseDailyRule(vCalRrule);
        parseMinutelyRule(vCalRrule);
    }

    private void parseYearlyRule(VCalRrule vCalRrule) {
        if (vCalRrule.yearly.parameters == null || vCalRrule.yearly.parameters.length <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (String str : vCalRrule.yearly.parameters) {
            if (vCalRrule.yearly.rule.equals(RecurrenceRuleParser.REPEAT_YEARLY_DAY)) {
                sb.append(str).append(RecurrenceRuleParser.VALUE_SEPARATOR);
            } else {
                sb2.append(str).append(RecurrenceRuleParser.VALUE_SEPARATOR);
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            this.byYrDayList = sb.toString();
        }
        if (sb2.length() > 0) {
            sb2.setLength(sb2.length() - 1);
            this.byMoList = sb2.toString();
        }
    }

    private void parseMonthlyRule(VCalRrule vCalRrule) {
        if (vCalRrule.monthly.parameters == null || vCalRrule.monthly.parameters.length <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        boolean zEquals = vCalRrule.monthly.rule.equals(RecurrenceRuleParser.REPEAT_MONTHLY_DAY);
        for (String str : vCalRrule.monthly.parameters) {
            if (zEquals) {
                sb = parseMonthDayList(sb, str);
            } else {
                sb2 = parseWeekDayList(sb2, str);
            }
        }
        if (sb2.length() > 0) {
            sb2.setLength(sb2.length() - 1);
            this.byWDayList = sb2.toString();
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            this.byMoDayList = sb.toString();
        }
    }

    private StringBuilder parseWeekDayList(StringBuilder sb, String str) {
        String strReplaceAll = Character.isDigit(str.charAt(0)) ? new StringBuilder(str).reverse().toString().replaceAll("\\+", "") : null;
        if (strReplaceAll != null) {
            sb.append(strReplaceAll);
        } else {
            sb.append(str).append(RecurrenceRuleParser.VALUE_SEPARATOR);
        }
        return sb;
    }

    private StringBuilder parseMonthDayList(StringBuilder sb, String str) {
        sb.append(str).append(RecurrenceRuleParser.VALUE_SEPARATOR);
        return sb;
    }

    private void parseWeeklyRule(VCalRrule vCalRrule) {
        if (vCalRrule.weekly.parameters == null || vCalRrule.weekly.parameters.length <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        int i = 0;
        for (String str : vCalRrule.weekly.parameters) {
            if (!Character.isDigit(str.charAt(0))) {
                if (i > 0) {
                    throw new IllegalArgumentException("Can't parse recurrence rule");
                }
                sb.append(str).append(RecurrenceRuleParser.VALUE_SEPARATOR);
            } else {
                i++;
                String strSubstring = str.substring(0, 2);
                String strSubstring2 = str.substring(2);
                sb2.append(strSubstring.replaceAll("0", "")).append(RecurrenceRuleParser.VALUE_SEPARATOR);
                if (!strSubstring2.equals("00")) {
                    sb3.append(strSubstring2).append(RecurrenceRuleParser.VALUE_SEPARATOR);
                }
            }
        }
        if (sb2.length() > 0) {
            sb2.setLength(sb2.length() - 1);
            this.byHrList = sb2.toString();
        }
        if (sb3.length() > 0) {
            sb3.setLength(sb3.length() - 1);
            this.byMinList = sb3.toString();
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            this.byWDayList = sb.toString();
        }
    }

    private void parseDailyRule(VCalRrule vCalRrule) {
        if (vCalRrule.daily.parameters == null || vCalRrule.daily.parameters.length <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : vCalRrule.daily.parameters) {
            sb.append(str.substring(0, 2).replaceAll("0", "")).append(RecurrenceRuleParser.VALUE_SEPARATOR);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            this.byHrList = sb.toString();
        }
    }

    private void parseMinutelyRule(VCalRrule vCalRrule) {
        if (vCalRrule.minutely.parameters == null || vCalRrule.minutely.parameters.length <= 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : vCalRrule.minutely.parameters) {
            sb.append(str).append(RecurrenceRuleParser.VALUE_SEPARATOR);
        }
        sb.setLength(sb.length() - 1);
        this.byMinList = sb.toString();
    }
}
