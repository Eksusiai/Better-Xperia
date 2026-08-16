package com.sonymobile.calendar;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.sonymobile.calendar.birthday.BirthdayActivity;
import com.sonymobile.calendar.utils.EventUtils;

/* JADX INFO: loaded from: classes2.dex */
public class WidgetProxyActivity extends AppCompatActivity {
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        intent.setAction("android.intent.action.VIEW");
        if (intent.hasExtra(BirthdayActivity.DISPLAYED_TIME)) {
            intent.setFlags(537001984);
            intent.setClass(this, BirthdayActivity.class);
            startActivity(intent);
        } else if (intent.hasExtra(EventUtils.START_ALARM)) {
            if (intent.getBooleanExtra(EventUtils.START_ALARM, false)) {
                EventUtils.startAlarmApp(this);
            }
        } else {
            intent.setFlags(537001984);
            if (Utils.isTabletDevice(this)) {
                intent.setClass(this, LaunchActivity.class);
            } else {
                intent.setClass(this, EventInfoActivity.class);
            }
            startActivity(intent);
        }
        finish();
    }
}
