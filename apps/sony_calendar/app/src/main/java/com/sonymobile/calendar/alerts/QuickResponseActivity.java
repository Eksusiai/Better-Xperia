package com.sonymobile.calendar.alerts;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import java.util.Arrays;

/* JADX INFO: loaded from: classes2.dex */
public class QuickResponseActivity extends PermissionHandlerAlertsActivity {
    static final String EXTRA_EVENT_ID = "eventId";
    static long mEventId;
    private String[] mResponses = null;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        long longExtra = intent.getLongExtra("eventId", -1L);
        mEventId = longExtra;
        if (longExtra == -1) {
            finish();
            return;
        }
        getListView().setOnItemClickListener(this);
        String[] quickResponses = Utils.getQuickResponses(this);
        Arrays.sort(quickResponses);
        this.mResponses = new String[quickResponses.length + 1];
        int i = 0;
        while (i < quickResponses.length) {
            this.mResponses[i] = quickResponses[i];
            i++;
        }
        this.mResponses[i] = getResources().getString(R.string.quick_response_custom_msg);
        setListAdapter(new ArrayAdapter(this, R.layout.quick_response_item, this.mResponses));
    }

    @Override // com.sonymobile.calendar.alerts.PermissionHandlerAlertsActivity
    public void onItemClick(int i) {
        String[] strArr = this.mResponses;
        new QueryThread(mEventId, (strArr == null || i >= strArr.length + (-1)) ? null : strArr[i]).start();
    }

    private class QueryThread extends Thread {
        String mBody;
        long mEventId;

        QueryThread(long j, String str) {
            this.mEventId = j;
            this.mBody = str;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Intent intentCreateEmailIntent = AlertReceiver.createEmailIntent(QuickResponseActivity.this, this.mEventId, this.mBody);
            if (intentCreateEmailIntent != null) {
                try {
                    QuickResponseActivity.this.startActivity(intentCreateEmailIntent);
                    QuickResponseActivity.this.finish();
                } catch (ActivityNotFoundException unused) {
                    QuickResponseActivity.this.getListView().post(new Runnable() { // from class: com.sonymobile.calendar.alerts.QuickResponseActivity.QueryThread.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Toast.makeText(QuickResponseActivity.this, R.string.quick_response_email_failed, 1).show();
                            QuickResponseActivity.this.finish();
                        }
                    });
                }
            }
        }
    }
}
