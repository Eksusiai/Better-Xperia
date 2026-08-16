package com.sonymobile.calendar;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class HintPromoteActivity extends AppCompatActivity {
    public static final String TAG_PROMOTION = "TAG_PROMOTION";

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!Utils.isTabletDevice(this)) {
            setRequestedOrientation(1);
        }
        setContentView(R.layout.hint_promote_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, HintPromoteFragment.newInstance(), TAG_PROMOTION).commit();
        }
        UiUtils.setNavigationBar(this);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        LinkedInUtils.handleSyncWithLinkedInResult(this, i, i2, intent);
    }
}
