package com.sonyericsson.calendar.util;

import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class VCalRrule {
    public VCalRrulePart daily;
    public VCalRrulePart minutely;
    public VCalRrulePart monthly;
    public String until;
    public VCalRrulePart weekly;
    public VCalRrulePart yearly;

    public static class VCalRrulePart {
        public String count;
        public String[] parameters;
        public String rule;
        public String value = "1";
    }

    public VCalRrule(RFC2445RRule rFC2445RRule) {
        initRules();
        parseRFCTags(rFC2445RRule);
    }

    public VCalRrule(String str) {
        checkForUnsupportedCharacters(str);
        initRules();
        splitParts(str);
    }

    private void checkForUnsupportedCharacters(String str) {
        if (str.contains(CalendarConstants.VCALENDAR_END_MARKER)) {
            throw new IllegalArgumentException("Can't parse recurrence rule");
        }
    }

    private void initRules() {
        this.yearly = new VCalRrulePart();
        this.monthly = new VCalRrulePart();
        this.weekly = new VCalRrulePart();
        this.daily = new VCalRrulePart();
        this.minutely = new VCalRrulePart();
    }

    private void splitParts(String str) {
        LinkedList linkedList = new LinkedList();
        StringBuilder sb = new StringBuilder();
        String str2 = null;
        for (String str3 : str.split(" ")) {
            if (partMatchesTag(str3)) {
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                    linkedList.add(sb.toString());
                    sb.setLength(0);
                }
                sb.append(str3).append(" ");
            } else if (isEndPartUntilValue(str3)) {
                str2 = str3;
            } else {
                sb.append(str3).append(" ");
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            linkedList.add(sb.toString());
        }
        if (str2 != null) {
            this.until = str2;
        }
        splitVCalRRuleParts(linkedList);
    }

    private boolean partMatchesTag(String str) {
        String strReplaceAll = str.replaceAll(RecurrenceRuleParser.PATTERN_NO_NUMBER, "");
        return strReplaceAll.equals("M") || strReplaceAll.equals(RecurrenceRuleParser.REPEAT_DAILY) || strReplaceAll.equals(RecurrenceRuleParser.REPEAT_WEEKLY) || strReplaceAll.equals(RecurrenceRuleParser.REPEAT_MONTHLY_DAY) || strReplaceAll.equals(RecurrenceRuleParser.REPEAT_MONTHLY_POS) || strReplaceAll.equals(RecurrenceRuleParser.REPEAT_YEARLY_MONTH) || strReplaceAll.equals(RecurrenceRuleParser.REPEAT_YEARLY_DAY);
    }

    private boolean isEndPartUntilValue(String str) {
        return str.endsWith(RecurrenceRuleParser.UNTIL_END_VALUE);
    }

    private void splitVCalRRuleParts(List<String> list) {
        for (String strSubstring : list) {
            String str = null;
            if (strSubstring.contains(RecurrenceRuleParser.COUNT_DELIMITER)) {
                int iIndexOf = strSubstring.indexOf(RecurrenceRuleParser.COUNT_DELIMITER);
                String strReplaceAll = strSubstring.substring(iIndexOf).replaceAll(" ", "");
                strSubstring = strSubstring.substring(0, iIndexOf - 1);
                str = strReplaceAll;
            }
            parseRuleValues(strSubstring.split(" "), str);
        }
    }

    private void parseRuleValues(String[] strArr, String str) {
        byte b = 0;
        String strReplaceAll = strArr[0].replaceAll(RecurrenceRuleParser.PATTERN_NO_NUMBER, "");
        String strReplaceAll2 = strArr[0].replaceAll(RecurrenceRuleParser.PATTERN_NUMBER, "");
        strReplaceAll.hashCode();
        switch (strReplaceAll.hashCode()) {
            case 68:
                if (!strReplaceAll.equals(RecurrenceRuleParser.REPEAT_DAILY)) {
                    b = -1;
                }
                break;
            case 77:
                b = !strReplaceAll.equals("M") ? (byte) -1 : (byte) 1;
                break;
            case 87:
                b = !strReplaceAll.equals(RecurrenceRuleParser.REPEAT_WEEKLY) ? (byte) -1 : (byte) 2;
                break;
            case 2455:
                b = !strReplaceAll.equals(RecurrenceRuleParser.REPEAT_MONTHLY_DAY) ? (byte) -1 : (byte) 3;
                break;
            case 2467:
                b = !strReplaceAll.equals(RecurrenceRuleParser.REPEAT_MONTHLY_POS) ? (byte) -1 : (byte) 4;
                break;
            case 2827:
                b = !strReplaceAll.equals(RecurrenceRuleParser.REPEAT_YEARLY_DAY) ? (byte) -1 : (byte) 5;
                break;
            case 2836:
                b = !strReplaceAll.equals(RecurrenceRuleParser.REPEAT_YEARLY_MONTH) ? (byte) -1 : (byte) 6;
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
                insertPartValue(this.daily, strArr, str, strReplaceAll, strReplaceAll2);
                break;
            case 1:
                insertPartValue(this.minutely, strArr, str, strReplaceAll, strReplaceAll2);
                break;
            case 2:
                insertPartValue(this.weekly, strArr, str, strReplaceAll, strReplaceAll2);
                break;
            case 3:
            case 4:
                insertPartValue(this.monthly, strArr, str, strReplaceAll, strReplaceAll2);
                break;
            case 5:
            case 6:
                insertPartValue(this.yearly, strArr, str, strReplaceAll, strReplaceAll2);
                break;
        }
    }

    private void insertPartValue(VCalRrulePart vCalRrulePart, String[] strArr, String str, String str2, String str3) {
        vCalRrulePart.rule = str2;
        vCalRrulePart.value = str3;
        if (strArr.length > 1) {
            vCalRrulePart.parameters = new String[strArr.length - 1];
            System.arraycopy(strArr, 1, vCalRrulePart.parameters, 0, vCalRrulePart.parameters.length);
        }
        vCalRrulePart.count = str;
    }

    private void parseRFCTags(RFC2445RRule rFC2445RRule) {
        parseRruleFrequency(rFC2445RRule);
        if (rFC2445RRule.until != null) {
            this.until = rFC2445RRule.until;
        }
    }

    private void parseRruleFrequency(RFC2445RRule rFC2445RRule) {
        if (rFC2445RRule.byYrDayList != null) {
            this.yearly.rule = RecurrenceRuleParser.REPEAT_YEARLY_DAY;
            this.yearly.parameters = rFC2445RRule.byYrDayList.split(RecurrenceRuleParser.VALUE_SEPARATOR);
            this.yearly.count = rFC2445RRule.count;
            this.yearly.value = getInterval(rFC2445RRule);
        } else if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.YEARLY)) {
            this.yearly.rule = RecurrenceRuleParser.REPEAT_YEARLY_DAY;
        }
        if (rFC2445RRule.byMoDayList != null) {
            this.monthly.rule = RecurrenceRuleParser.REPEAT_MONTHLY_DAY;
            this.monthly.parameters = rFC2445RRule.byMoDayList.split(RecurrenceRuleParser.VALUE_SEPARATOR);
            this.monthly.count = rFC2445RRule.count;
            this.monthly.value = rFC2445RRule.interval;
        } else if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.MONTHLY)) {
            this.monthly.rule = RecurrenceRuleParser.REPEAT_MONTHLY_POS;
            this.monthly.count = rFC2445RRule.count;
            this.monthly.value = getInterval(rFC2445RRule);
        }
        if (rFC2445RRule.byMoList != null) {
            this.yearly.rule = RecurrenceRuleParser.REPEAT_YEARLY_MONTH;
            this.yearly.parameters = rFC2445RRule.byMoList.split(RecurrenceRuleParser.VALUE_SEPARATOR);
            this.yearly.count = rFC2445RRule.count;
            this.yearly.value = getInterval(rFC2445RRule);
        }
        if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.WEEKLY)) {
            this.weekly.rule = RecurrenceRuleParser.REPEAT_WEEKLY;
            this.weekly.count = rFC2445RRule.count;
            this.weekly.value = getInterval(rFC2445RRule);
        }
        if (rFC2445RRule.byWDayList != null) {
            parseByWeekDayList(rFC2445RRule);
        }
        if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.DAILY)) {
            this.daily.rule = RecurrenceRuleParser.REPEAT_DAILY;
            this.daily.value = getInterval(rFC2445RRule);
        }
        if (rFC2445RRule.byHrList != null) {
            this.daily.rule = RecurrenceRuleParser.REPEAT_DAILY;
            this.daily.count = rFC2445RRule.count;
            this.daily.value = getInterval(rFC2445RRule);
            this.daily.parameters = rFC2445RRule.byHrList.split(RecurrenceRuleParser.VALUE_SEPARATOR);
        } else if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.BYHOUR)) {
            this.daily.rule = RecurrenceRuleParser.REPEAT_DAILY;
        }
        if (rFC2445RRule.byMinList != null) {
            this.minutely.rule = "M";
            this.minutely.value = getInterval(rFC2445RRule);
            this.minutely.count = rFC2445RRule.count;
            this.minutely.parameters = rFC2445RRule.byMinList.split(RecurrenceRuleParser.VALUE_SEPARATOR);
        } else if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.BYMINUTE)) {
            this.minutely.rule = "M";
        }
        if (rFC2445RRule.bySpList != null) {
            throw new IllegalArgumentException("Can't parse recurrence rule");
        }
    }

    private void parseByWeekDayList(RFC2445RRule rFC2445RRule) {
        String str;
        String[] strArrSplit = rFC2445RRule.byWDayList.split(RecurrenceRuleParser.VALUE_SEPARATOR);
        String[] strArr = new String[strArrSplit.length];
        for (int i = 0; i < strArrSplit.length; i++) {
            if (strArrSplit[i].matches(".*\\d.*")) {
                String strSubstring = strArrSplit[i].substring(0, strArrSplit[i].length() - 2);
                if (strSubstring.startsWith(CalendarConstants.HYPHEN)) {
                    str = strSubstring + CalendarConstants.HYPHEN;
                } else {
                    str = strSubstring + "+";
                }
                strArr[i] = str + " " + strArrSplit[i].substring(strArrSplit[i].length() - 2);
            } else {
                strArr[i] = strArrSplit[i];
            }
        }
        if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.YEARLY)) {
            throw new IllegalArgumentException("Can't parse recurrence rule");
        }
        if (rFC2445RRule.frequency.equals(RecurrenceRuleParser.MONTHLY)) {
            this.monthly.rule = RecurrenceRuleParser.REPEAT_MONTHLY_DAY;
            this.monthly.count = rFC2445RRule.count;
            this.monthly.value = getInterval(rFC2445RRule);
            this.monthly.parameters = strArr;
            return;
        }
        this.weekly.rule = RecurrenceRuleParser.REPEAT_WEEKLY;
        this.weekly.count = rFC2445RRule.count;
        this.weekly.value = getInterval(rFC2445RRule);
        this.weekly.parameters = strArr;
    }

    private String getInterval(RFC2445RRule rFC2445RRule) {
        return rFC2445RRule.interval == null ? "1" : rFC2445RRule.interval;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(appendString(this.yearly));
        sb.append(appendString(this.monthly));
        sb.append(appendString(this.weekly));
        sb.append(appendString(this.daily));
        sb.append(appendString(this.minutely));
        String str = this.until;
        if (str != null) {
            sb.append(str).append(" ");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private String appendString(VCalRrulePart vCalRrulePart) {
        StringBuilder sb = new StringBuilder();
        if (vCalRrulePart.rule != null) {
            sb.append(vCalRrulePart.rule).append(vCalRrulePart.value).append(" ");
            if (vCalRrulePart.parameters != null) {
                for (String str : vCalRrulePart.parameters) {
                    sb.append(str).append(" ");
                }
            }
            if (vCalRrulePart.count != null) {
                sb.append(RecurrenceRuleParser.COUNT_DELIMITER).append(vCalRrulePart.count).append(" ");
            }
        }
        return sb.toString();
    }
}
