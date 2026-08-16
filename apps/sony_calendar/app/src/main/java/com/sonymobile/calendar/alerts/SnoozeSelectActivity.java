package com.sonymobile.calendar.alerts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.ArrayAdapter;
import androidx.core.app.NotificationManagerCompat;
import com.sonymobile.calendar.R;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class SnoozeSelectActivity extends PermissionHandlerAlertsActivity {
    private static final int[] array_snooze_time = {5, 10, 15, 30};
    private long mBeginTime;
    private long mEndTime;
    private long mEventId = -1;
    private int mNotificationId = -1;
    private boolean mIsLunarEvent = false;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        this.mEventId = intent.getLongExtra(AlertUtils.EVENT_ID_KEY, -1L);
        this.mBeginTime = intent.getLongExtra(AlertUtils.EVENT_START_KEY, 0L);
        this.mEndTime = intent.getLongExtra(AlertUtils.EVENT_END_KEY, 0L);
        this.mNotificationId = intent.getIntExtra("notificationid", -1);
        this.mIsLunarEvent = intent.getBooleanExtra(AlertUtils.IS_LUNAR_EVENT, false);
        String string = getResources().getString(R.string.snooze_menu_labels);
        String[] strArr = new String[4];
        for (int i = 0; i < 4; i++) {
            strArr[i] = String.format(string, Integer.valueOf(array_snooze_time[i]));
        }
        setListAdapter(new ArrayAdapter(this, R.layout.snooze_select_item, strArr));
        getListView().setOnItemClickListener(this);
    }

    @Override // com.sonymobile.calendar.alerts.PermissionHandlerAlertsActivity
    public void onItemClick(int i) {
        NotificationManagerCompat.from(this).cancel(this.mNotificationId);
        ContentResolver contentResolver = getContentResolver();
        Uri uri = this.mIsLunarEvent ? LunarContract.CalendarAlerts.CONTENT_URI : CalendarContract.CalendarAlerts.CONTENT_URI;
        ContentValues contentValues = new ContentValues();
        contentValues.put("state", (Integer) 2);
        contentResolver.update(uri, contentValues, "event_id=" + this.mEventId + " AND state=1", null);
        long jCurrentTimeMillis = System.currentTimeMillis() + (((long) array_snooze_time[i]) * 60 * 1000);
        contentValues.clear();
        contentResolver.insert(uri, AlertUtils.makeContentValues(this.mEventId, this.mBeginTime, this.mEndTime, jCurrentTimeMillis, 0));
        AlertUtils.scheduleAlarm(this, null, jCurrentTimeMillis, this.mIsLunarEvent);
        finish();
    }
}
