package com.sonymobile.calendar.birthday;
import com.sonymobile.calendar.SafeTime;

import android.text.format.Time;
import android.util.Log;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.DaySpan;
import com.sonymobile.calendar.PhoneNumber;
import com.sonymobile.calendar.widget.CalendarAppWidgetModel;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class ContactBirthday {
    public static final int MINIMAL_AGE = 0;
    public static final int NO_YEAR_SPECIFIED = 0;
    public int age;
    public String contactId;
    public boolean hasPhoneNumber;
    public int month;
    public int monthDay;
    public String name;
    public ArrayList<PhoneNumber> phoneNumbers;
    public int year = 0;
    public String primaryNumber = "";

    public ContactBirthday(String str, String str2) {
        this.name = str;
        parseDay(str2);
    }

    public boolean isBirthdayWithinSpan(DaySpan daySpan) {
        Time time = new SafeTime();
        time.setJulianDay(daySpan.startJulianDay);
        Time time2 = new SafeTime();
        time2.setJulianDay(daySpan.endJulianDay);
        if (this.year == 0) {
            Time time3 = new SafeTime(time.timezone);
            time3.set(this.monthDay, this.month, time.year);
            time3.normalize(false);
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time3.timezone));
            if (((calendar.getTimeInMillis() + ((long) (calendar.get(15) + calendar.get(16)))) - time3.toMillis(false)) / 86400000 > 365) {
                return false;
            }
        }
        return (this.month >= time.month && this.month <= time2.month) && (this.monthDay >= time.monthDay && this.monthDay <= time2.monthDay);
    }

    public int getAge(Time time) {
        return getAge(time.year);
    }

    public int getAge(int i) {
        int i2 = this.year;
        if (i2 > 0) {
            return i - i2;
        }
        return 0;
    }

    public int hashCode() {
        String str = this.contactId;
        return 31 + (str == null ? 0 : str.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ContactBirthday contactBirthday = (ContactBirthday) obj;
        String str = this.contactId;
        if (str == null) {
            if (contactBirthday.contactId != null) {
                return false;
            }
        } else if (!str.equals(contactBirthday.contactId)) {
            return false;
        }
        return true;
    }

    public void mergeContact(ContactBirthday contactBirthday) {
        if (this.primaryNumber.length() < 1) {
            this.primaryNumber = contactBirthday.primaryNumber;
        }
    }

    private void parseDay(String str) {
        try {
            if (str == null) {
                throw new InvalidParameterException("Birthday string returned from cursor is null");
            }
            if (str.contains(CalendarConstants.HYPHEN)) {
                String[] strArrSplit = str.split(CalendarConstants.HYPHEN);
                if (strArrSplit.length <= 3 && strArrSplit[0].length() >= 2) {
                    if (strArrSplit.length == 3) {
                        this.year = Integer.parseInt(strArrSplit[0]);
                        this.month = Integer.parseInt(strArrSplit[1]) - 1;
                        this.monthDay = Integer.parseInt(strArrSplit[2]);
                        return;
                    }
                    return;
                }
                this.monthDay = Integer.parseInt(strArrSplit[strArrSplit.length - 1]);
                this.month = Integer.parseInt(strArrSplit[strArrSplit.length - 2]) - 1;
                return;
            }
            String[] strArrSplit2 = str.split("/");
            if (strArrSplit2.length > 1) {
                this.month = Integer.parseInt(strArrSplit2[0]) - 1;
                this.monthDay = Integer.parseInt(strArrSplit2[1]);
                if (strArrSplit2.length == 3) {
                    this.year = Integer.parseInt(strArrSplit2[2]);
                }
            }
        } catch (NumberFormatException | InvalidParameterException e) {
            Log.e("ContactBirthday", "birthDay string is invalid", e);
            this.year = 0;
            this.month = 0;
            this.monthDay = 0;
        }
    }

    public int getYearMonthDay() {
        return CalendarAppWidgetModel.DayInfo.yearMonthDay(this.year, this.month, this.monthDay);
    }
}
