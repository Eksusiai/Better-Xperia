package com.sonymobile.calendar;

import android.content.Intent;
import android.text.format.Time;
import android.view.MenuItem;
import com.sonymobile.calendar.datetimepicker.date.DatePickerDialog;
import com.sonymobile.calendar.tablet.TabletCalendarPreferenceActivity;
import com.sonymobile.lunar.lib.LunarContract;
import java.util.Calendar;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes2.dex */
public class MenuHelper {
    public static final int MENU_EVENT_CREATE = 5;
    public static final int MENU_EVENT_DELETE = 7;
    public static final int MENU_EVENT_EDIT = 6;
    public static final int MENU_EVENT_VIEW = 4;

    public static boolean onOptionsItemSelected(LaunchActivity launchActivity, MenuItem menuItem, final Navigator navigator) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            launchActivity.finish();
            return true;
        }
        if (itemId == R.id.action_conversion) {
            final String timeZone = Utils.getTimeZone(launchActivity, null);
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            calendar.setTimeInMillis(Utils.getDisplayTime());
            DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() { // from class: com.sonymobile.calendar.MenuHelper.1
                @Override // com.sonymobile.calendar.datetimepicker.date.DatePickerDialog.OnDateSetListener
                public void onDateSet(DatePickerDialog datePickerDialog, int i, int i2, int i3) {
                    calendar.set(i, i2, i3);
                    Utils.setDisplayTime(Long.valueOf(calendar.getTimeInMillis()));
                    Time time = new SafeTime(timeZone);
                    time.set(calendar.getTimeInMillis());
                    navigator.goTo(time, true);
                }
            }, calendar.get(1), calendar.get(2), calendar.get(5), 0).show(launchActivity.getSupportFragmentManager(), (String) null);
            return true;
        }
        if (itemId == R.id.action_settings) {
            Intent intent = new Intent("android.intent.action.VIEW");
            if (Utils.isTabletDevice(launchActivity)) {
                intent.setClassName(launchActivity, TabletCalendarPreferenceActivity.class.getName());
            } else {
                intent.setClassName(launchActivity, CalendarSettingsActivity.class.getName());
            }
            intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, navigator.getSelectedTimeInMillis());
            intent.setFlags(537001984);
            launchActivity.startActivity(intent);
            return true;
        }
        if (itemId == R.id.action_today) {
            Utils.setAgendaSelectedEventInstanceId(0L);
            navigator.goToToday();
            return true;
        }
        return false;
    }
}
