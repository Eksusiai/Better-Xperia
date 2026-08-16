package com.sonymobile.calendar;

import android.text.format.Time;
import java.util.Calendar;
import java.util.TimeZone;

public class SafeTime extends Time {
    private static final String TAG = "TIMEPROBE";

    public SafeTime() {
        super();
    }

    public SafeTime(Time other) {
        super(other);
    }

    public SafeTime(String timezone) {
        super(timezone);
    }

    @Override
    public long toMillis(boolean ignoreDst) {
        return TimeUtils.toMillis(this, ignoreDst);
    }

    @Override
    public void set(long millis) {
        TimeUtils.setMillis(this, millis);
    }

    @Override
    public long normalize(boolean ignoreDst) {
        TimeUtils.normalize(this, ignoreDst);
        return toMillis(ignoreDst);
    }

    public static TimeZone getTimeZone(Time time) {
        try {
            if (time.timezone != null && time.timezone.length() > 0) {
                return TimeZone.getTimeZone(time.timezone);
            }
        } catch (Exception unused) {
        }
        return TimeZone.getDefault();
    }
}
