package com.sonymobile.calendar.widget;
import com.sonymobile.calendar.SafeTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.CalendarConstants;
import com.sonyericsson.calendar.util.FreeDayService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.BuildConfig;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.LaunchActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.WidgetProxyActivity;
import com.sonymobile.calendar.birthday.BirthdayActivity;
import com.sonymobile.calendar.jobs.ProviderChangeJobService;
import com.sonymobile.calendar.lunar.LunarAvailabilityManager;
import com.sonymobile.calendar.utils.EventUtils;
import com.sonymobile.lunar.lib.LunarContract;
import com.sonymobile.lunar.lib.LunarUtils;
import java.util.Calendar;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarAppWidgetProvider extends AppWidgetProvider {
    private static final String APPWIDGET_SCROLL_TO_TOP = "com.sonymobile.calendar.APPWIDGET_SCROLL_TO_TOP";
    public static final String APPWIDGET_TRANSPARENCY_VALUE_CHANGED = "com.sonymobile.calendar.APPWIDGET_TRANSPARENCY_VALUE_CHANGED";
    static final String EXTRA_EVENT_IDS = "com.sonymobile.calendar.EXTRA_EVENT_IDS";
    public static final String KEY_APP_WIDGET_ID = "app_widget_id";
    public static final String KEY_CURRENT_TIME_INTRINSIC = "TAKE_CURRENT_TIME_INTRINSIC";
    static final boolean LOGD = true;
    static final float SCALE_DOWN_TRANSPARENCY_FACTOR = 1.5f;
    static final String TAG = "CalAppWidgetProvider";
    static final int TRANSPARENCY_MAX_VALUE = 255;

    @Override // android.appwidget.AppWidgetProvider, android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (APPWIDGET_SCROLL_TO_TOP.equals(action)) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setScrollPosition(R.id.events_list, 0);
            AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(intent.getIntExtra("appWidgetId", 0), remoteViews);
            return;
        }
        Log.d(TAG, "AppWidgetProvider got the intent: " + intent.toString());
        if (Utils.getWidgetUpdateAction(context).equals(action) || action.equals(APPWIDGET_TRANSPARENCY_VALUE_CHANGED) || action.equals("android.intent.action.LOCALE_CHANGED")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            performUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(getComponentName(context)), null);
        } else if (action.equals("android.intent.action.PROVIDER_CHANGED") || action.equals("android.intent.action.TIME_SET") || action.equals("android.intent.action.TIMEZONE_CHANGED") || action.equals("android.intent.action.DATE_CHANGED") || action.equals(Utils.getWidgetScheduledUpdateAction(context))) {
            context.startService(new Intent(context, (Class<?>) CalendarAppWidgetService.class));
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onEnabled(Context context) {
        ProviderChangeJobService.scheduleJob(context, 5);
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onDisabled(Context context) {
        ((AlarmManager) context.getSystemService("alarm")).cancel(getUpdateIntent(context));
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        performUpdate(context, appWidgetManager, iArr, null);
    }

    static ComponentName getComponentName(Context context) {
        return new ComponentName(context, (Class<?>) CalendarAppWidgetProvider.class);
    }

    /* JADX WARN: Code duplicated, block: B:25:0x018c  */
    /* JADX WARN: Code duplicated, block: B:26:0x0193  */
    private void performUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr, long[] jArr) {
        String dateString;
        int i;
        CharSequence charSequence;
        int i2;
        int[] iArr2 = iArr;
        int length = iArr2.length;
        int i3 = 0;
        while (i3 < length) {
            int i4 = iArr2[i3];
            Log.d(TAG, "Building widget update...");
            Intent intent = new Intent(context, (Class<?>) CalendarAppWidgetService.class);
            intent.putExtra("appWidgetId", i4);
            if (jArr != null) {
                intent.putExtra(EXTRA_EVENT_IDS, jArr);
            }
            intent.setData(Uri.parse(intent.toUri(1)));
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            Time time = new SafeTime(Utils.getTimeZone(context, null));
            time.setToNow();
            long millis = time.toMillis(true);
            boolean zEquals = context.getResources().getConfiguration().locale.toString().equals("zh_CN_#Hans");
            String str = Utils.FORMAT_DAY_DASH_MONTH + context.getResources().getString(R.string.day_view) + CalendarConstants.HYPHEN + Utils.FORMAT_DATE_YEAR;
            if (zEquals) {
                dateString = Utils.getDateString(context, time, str);
            } else {
                dateString = Utils.getDateString(context, time, Utils.FORMAT_MONTH_DASH_YEAR);
            }
            String dateString2 = Utils.getDateString(context, time, Utils.FORMAT_WEEK_DAY_DASH_MONTH_DAY);
            if (LunarAvailabilityManager.isLunarAvailable(context)) {
                remoteViews.setViewVisibility(R.id.lunar_date, 0);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(millis);
                LunarUtils.LunarDate lunarDateConvertSolarDateToLunarDate = LunarUtils.convertSolarDateToLunarDate(calendar.getTime());
                remoteViews.setTextViewText(R.id.lunar_date, LunarUtils.getLunarYearInfo(calendar.getTime()) + " " + LunarUtils.getLunarMonthString(lunarDateConvertSolarDateToLunarDate) + LunarUtils.getLunarDayString(lunarDateConvertSolarDateToLunarDate));
                i = 8;
            } else {
                i = 8;
                remoteViews.setViewVisibility(R.id.lunar_date, 8);
            }
            remoteViews.setTextViewText(R.id.monthYearHeader, dateString);
            FreeDayService freeDayService = FreeDayService.getInstance();
            if (freeDayService.isDataLoaded()) {
                if (freeDayService.isFreeDay(time.year, time.month, time.monthDay)) {
                    int holidayColor = freeDayService.getHolidayColor(context);
                    remoteViews.setTextColor(R.id.currentDayNameNumber, holidayColor);
                    remoteViews.setTextColor(R.id.lunar_date, holidayColor);
                    length = length;
                    millis = millis;
                    remoteViews = remoteViews;
                    charSequence = null;
                    i3 = i3;
                    i2 = 8;
                    freeDayService.requestHolidayName(context, time.year, time.month, time.monthDay, new FreeDayResultHandler(context, i4, remoteViews), 1);
                } else {
                    i2 = 8;
                    charSequence = null;
                }
                if (TextUtils.isEmpty(charSequence)) {
                    remoteViews.setViewVisibility(R.id.todayTomorrow, i2);
                    remoteViews.setViewVisibility(R.id.dashView, i2);
                } else {
                    remoteViews.setViewVisibility(R.id.todayTomorrow, 0);
                    remoteViews.setViewVisibility(R.id.dashView, 0);
                    remoteViews.setTextViewText(R.id.todayTomorrow, charSequence);
                }
                remoteViews.setTextViewText(R.id.currentDayNameNumber, dateString2);
                remoteViews.setRemoteAdapter(i4, R.id.events_list, intent);
                appWidgetManager.notifyAppWidgetViewDataChanged(i4, R.id.events_list);
                Intent intent2 = new Intent("android.intent.action.VIEW");
                intent2.setClass(context, LaunchActivity.class);
                intent2.setFlags(537001984);
                intent2.setData(Uri.parse("content://com.sonymobile.calendar/time/" + millis));
                remoteViews.setOnClickPendingIntent(R.id.header, PendingIntent.getActivity(context, 0, intent2, 67108864));
                Intent intent3 = new Intent(APPWIDGET_SCROLL_TO_TOP);
                intent3.setComponent(new ComponentName(BuildConfig.APPLICATION_ID, "com.sonymobile.calendar.widget.CalendarAppWidgetProvider"));
                intent3.putExtra("appWidgetId", i4);
                remoteViews.setOnClickPendingIntent(R.id.todayArrowCircle, PendingIntent.getBroadcast(context, i4, intent3, 67108864));
                remoteViews.setPendingIntentTemplate(R.id.events_list, getLaunchPendingIntentTemplate(context));
                Intent intent4 = new Intent("android.intent.action.INSERT");
                intent4.setClass(context, LaunchActivity.class);
                intent4.setFlags(537001984);
                intent4.putExtra(KEY_CURRENT_TIME_INTRINSIC, false);
                intent4.putExtra(KEY_APP_WIDGET_ID, i4);
                remoteViews.setOnClickPendingIntent(R.id.addEventButton, PendingIntent.getActivity(context, i4, intent4, 201326592));
                setWidgetBackgroundColor(context, remoteViews);
                appWidgetManager.updateAppWidget(i4, remoteViews);
                i3++;
                iArr2 = iArr;
                length = length;
            } else {
                charSequence = null;
                i2 = i;
            }
            freeDayService.requestLoad(context, new FreeDayResultHandler(context, i4), 0, false);
            int color = ContextCompat.getColor(context, R.color.white);
            remoteViews.setTextColor(R.id.currentDayNameNumber, color);
            remoteViews.setTextColor(R.id.lunar_date, color);
            if (TextUtils.isEmpty(charSequence)) {
                remoteViews.setViewVisibility(R.id.todayTomorrow, i2);
                remoteViews.setViewVisibility(R.id.dashView, i2);
            } else {
                remoteViews.setViewVisibility(R.id.todayTomorrow, 0);
                remoteViews.setViewVisibility(R.id.dashView, 0);
                remoteViews.setTextViewText(R.id.todayTomorrow, charSequence);
            }
            remoteViews.setTextViewText(R.id.currentDayNameNumber, dateString2);
            remoteViews.setRemoteAdapter(i4, R.id.events_list, intent);
            appWidgetManager.notifyAppWidgetViewDataChanged(i4, R.id.events_list);
            Intent intent5 = new Intent("android.intent.action.VIEW");
            intent5.setClass(context, LaunchActivity.class);
            intent5.setFlags(537001984);
            intent5.setData(Uri.parse("content://com.sonymobile.calendar/time/" + millis));
            remoteViews.setOnClickPendingIntent(R.id.header, PendingIntent.getActivity(context, 0, intent5, 67108864));
            Intent intent6 = new Intent(APPWIDGET_SCROLL_TO_TOP);
            intent6.setComponent(new ComponentName(BuildConfig.APPLICATION_ID, "com.sonymobile.calendar.widget.CalendarAppWidgetProvider"));
            intent6.putExtra("appWidgetId", i4);
            remoteViews.setOnClickPendingIntent(R.id.todayArrowCircle, PendingIntent.getBroadcast(context, i4, intent6, 67108864));
            remoteViews.setPendingIntentTemplate(R.id.events_list, getLaunchPendingIntentTemplate(context));
            Intent intent7 = new Intent("android.intent.action.INSERT");
            intent7.setClass(context, LaunchActivity.class);
            intent7.setFlags(537001984);
            intent7.putExtra(KEY_CURRENT_TIME_INTRINSIC, false);
            intent7.putExtra(KEY_APP_WIDGET_ID, i4);
            remoteViews.setOnClickPendingIntent(R.id.addEventButton, PendingIntent.getActivity(context, i4, intent7, 201326592));
            setWidgetBackgroundColor(context, remoteViews);
            appWidgetManager.updateAppWidget(i4, remoteViews);
            i3++;
            iArr2 = iArr;
            length = length;
        }
    }

    static PendingIntent getUpdateIntent(Context context) {
        Intent intent = new Intent(Utils.getWidgetScheduledUpdateAction(context));
        intent.setDataAndType(CalendarContract.CONTENT_URI, Utils.APPWIDGET_DATA_TYPE);
        return PendingIntent.getBroadcast(context, 0, intent, 67108864);
    }

    static PendingIntent getLaunchPendingIntentTemplate(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WidgetProxyActivity.class);
        if (Build.VERSION.SDK_INT >= 31) {
            return PendingIntent.getActivity(context, 0, intent, 167772160);
        }
        return PendingIntent.getActivity(context, 0, intent, 134217728);
    }

    static Intent getLaunchFillInIntent(Context context, long j, long j2, long j3, boolean z, long j4) {
        Uri uriWithAppendedId;
        Intent intent = new Intent();
        if (z) {
            uriWithAppendedId = Uri.parse("content://com.sonymobile.lunar/events");
        } else {
            uriWithAppendedId = CalendarContract.Events.CONTENT_URI;
        }
        if (j != 0) {
            intent.putExtra(Utils.INTENT_KEY_DETAIL_VIEW, true);
            uriWithAppendedId = ContentUris.withAppendedId(uriWithAppendedId, j);
        }
        intent.setData(uriWithAppendedId);
        intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, j2);
        intent.putExtra(LunarContract.EXTRA_EVENT_END_TIME, j3);
        intent.putExtra("_id", j4);
        intent.setFlags(32768);
        intent.setFlags(524288);
        return intent;
    }

    static Intent getLaunchAlarmIntent() {
        Intent intent = new Intent();
        intent.putExtra(EventUtils.START_ALARM, true);
        return intent;
    }

    static Intent getBirthdayFillInIntent(Context context, long j) {
        Intent intent = new Intent();
        intent.putExtra(BirthdayActivity.DISPLAYED_TIME, j);
        intent.setFlags(32768);
        intent.addFlags(524288);
        return intent;
    }

    private void setWidgetBackgroundColor(Context context, RemoteViews remoteViews) {
        int iRound = 255 - Math.round((PreferenceManager.getDefaultSharedPreferences(context).getInt(GeneralPreferences.KEY_WIDGET_TRANSPARENCY, context.getResources().getInteger(R.integer.widget_default_transparency)) * 255) / 100.0f);
        remoteViews.setInt(R.id.events_list, "setBackgroundColor", setTransparency(context, iRound, R.color.widgetListItemBackground));
        int i = (int) (iRound * SCALE_DOWN_TRANSPARENCY_FACTOR);
        remoteViews.setInt(R.id.full_header, "setBackgroundColor", setTransparency(context, i <= 255 ? i : 255, R.color.widgetHeaderBackground));
    }

    private int setTransparency(Context context, int i, int i2) {
        int color = ContextCompat.getColor(context, i2);
        return Color.argb(i, Color.red(color), Color.green(color), Color.blue(color));
    }

    private static class FreeDayResultHandler implements IAsyncServiceResultHandler {
        private Context context;
        private RemoteViews remoteViews;
        private int widgetId;

        public FreeDayResultHandler(Context context, int i) {
            this.context = context;
            this.widgetId = i;
        }

        public FreeDayResultHandler(Context context, int i, RemoteViews remoteViews) {
            this.context = context;
            this.widgetId = i;
            this.remoteViews = remoteViews;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (obj != null) {
                int iIntValue = ((Integer) obj2).intValue();
                if (iIntValue != 0) {
                    if (iIntValue != 1) {
                        return;
                    }
                    AppWidgetManager.getInstance(this.context).notifyAppWidgetViewDataChanged(this.widgetId, R.id.header);
                } else if (((Boolean) obj).booleanValue()) {
                    AppWidgetManager.getInstance(this.context).notifyAppWidgetViewDataChanged(this.widgetId, R.id.header);
                }
            }
        }
    }
}
