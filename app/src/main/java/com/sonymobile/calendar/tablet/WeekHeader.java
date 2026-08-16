package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.AttributeSet;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class WeekHeader extends HeaderBase {
    public WeekHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.sonymobile.calendar.tablet.HeaderBase
    protected String getText(Context context, Time time) {
        String headerText = Utils.getHeaderText(context, time, 2);
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(GeneralPreferences.KEY_SHOW_WEEK_NUMBER, false)) {
            return headerText;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(time.timezone));
        calendar.setTimeInMillis(time.toMillis(false));
        return appendWeekText(calendar, headerText);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        update(getContext(), this.displayedTime);
    }

    private String appendWeekText(Calendar calendar, String str) {
        String string = getContext().getString(R.string.week_view);
        int weekNumberOfDay = Utils.getWeekNumberOfDay(getContext(), calendar);
        if (string.length() <= 0) {
            return "";
        }
        if (Locale.getDefault().getLanguage().equals("ja") || Locale.getDefault().getLanguage().equals("ko")) {
            return weekNumberOfDay + " " + string + ", " + str;
        }
        if (Locale.getDefault().getLanguage().equals("lv")) {
            return str + ", " + weekNumberOfDay + ". " + string;
        }
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return str + ", " + weekNumberOfDay + string;
        }
        return str + ", " + string + " " + weekNumberOfDay;
    }
}
